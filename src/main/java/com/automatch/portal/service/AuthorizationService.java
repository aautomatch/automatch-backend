package com.automatch.portal.service;

import com.automatch.portal.dao.UserDAO;
import com.automatch.portal.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserModel> userOptional = userDAO.findByEmail(username);

        if (userOptional.isEmpty()) {
            throw new UsernameNotFoundException("Usuário não encontrado com email: " + username);
        }

        UserModel user = userOptional.get();

        if (Boolean.FALSE.equals(user.getIsActive())) {
            throw new UsernameNotFoundException("Usuário inativo: " + username);
        }

        if (user.getDeletedAt() != null) {
            throw new UsernameNotFoundException("Usuário deletado: " + username);
        }

        return user;
    }
}