package com.inhabas.api.auth.domain.token.jwtUtils;

import com.inhabas.api.auth.domain.token.exception.InvalidTokenException;
import com.inhabas.api.auth.domain.token.TokenDto;
import com.inhabas.api.auth.domain.token.TokenUtil;
import com.inhabas.api.auth.domain.token.TokenReIssuer;
import com.inhabas.api.auth.domain.token.TokenResolver;
import com.inhabas.api.auth.domain.token.jwtUtils.refreshToken.RefreshTokenNotFoundException;
import com.inhabas.api.auth.domain.token.jwtUtils.refreshToken.RefreshTokenRepository;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtTokenReIssuer implements TokenReIssuer {

    private final TokenUtil tokenUtil;
    private final TokenResolver tokenResolver;
    private final RefreshTokenRepository refreshTokenRepository;


    @Override
    public TokenDto reissueAccessToken(String refreshToken) throws InvalidTokenException, RefreshTokenNotFoundException {

        if (!tokenUtil.validate(refreshToken) ) {
            throw new InvalidTokenException();
        }

        if (!refreshTokenRepository.existsByRefreshToken(refreshToken)) {
            throw new RefreshTokenNotFoundException();
        }

        return tokenUtil.reissueAccessTokenUsing(refreshToken);
    }
}
