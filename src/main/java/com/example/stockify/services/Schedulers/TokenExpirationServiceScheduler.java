package com.example.stockify.services.Schedulers;

import com.example.stockify.entities.AuthTokenEntity;
import com.example.stockify.repositories.AuthTokenRepository;
import com.example.stockify.services.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class TokenExpirationServiceScheduler {

    private final AuthTokenRepository authTokenRepository;
    private final JwtService jwtService ;

    public TokenExpirationServiceScheduler(AuthTokenRepository authTokenRepository, JwtService jwtService) {
        this.authTokenRepository = authTokenRepository;
        this.jwtService = jwtService;
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void checkExpirationToken(){
        List<AuthTokenEntity> activeTokens = authTokenRepository.findByIsExpiredFalse();
        for(AuthTokenEntity authToken : activeTokens){
            String username =authToken.getUser().getUsername();
            try {
                boolean expired = jwtService.isTokenExpired(authToken.getToken());
                System.out.println(  "User: " + username +" JWT expired: " + expired );
                if (expired) {
                    authToken.setExpired(true);
                    authTokenRepository.save(authToken);
                    System.out.println(  "Token marked expired for user: "+ username);
                }
            }catch (Exception e) {
                System.out.println(
                        "Error checking token for user "
                                + username + ": "
                                + e.getMessage()
                );
            }
        }
    }
}
