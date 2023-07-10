package com.a503.churros.service.user;

import com.a503.churros.config.security.UserPrincipal;
import com.a503.churros.entity.user.User;
import com.a503.churros.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;


import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("유저 정보를 찾을 수 없습니다.")
                );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
//        DefaultAssert.isOptionalPresent(user);

        return UserPrincipal.create(user.get());
    }
}
