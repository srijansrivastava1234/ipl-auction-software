package com.ipl.auction.service;

import com.ipl.auction.config.TokenUtil;
import com.ipl.auction.dto.LoginRequest;
import com.ipl.auction.dto.LoginResponse;
import com.ipl.auction.model.User;
import com.ipl.auction.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, TokenUtil tokenUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenUtil = tokenUtil;
    }

    public LoginResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        String token = tokenUtil.generateToken(user);
        Long teamId = user.getTeam() != null ? user.getTeam().getId() : null;
        String teamName = user.getTeam() != null ? user.getTeam().getName() : null;

        return new LoginResponse(token, user.getUsername(), user.getRole(), teamId, teamName);
    }

    public User register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
