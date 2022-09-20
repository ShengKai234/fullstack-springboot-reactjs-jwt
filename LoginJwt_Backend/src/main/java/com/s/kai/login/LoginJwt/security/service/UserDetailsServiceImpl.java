package com.s.kai.login.LoginJwt.security.service;

import com.s.kai.login.LoginJwt.models.User;
import com.s.kai.login.LoginJwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// If the authentication process is successful, we can get User’s information such as username, password, authorities from an Authentication object.
// full custom User object using UserRepository
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));
        // (!!!convert Set<Role> into List<GrantedAuthority>!!!)
        return UserDetailsImpl.build(user);
    }
}
