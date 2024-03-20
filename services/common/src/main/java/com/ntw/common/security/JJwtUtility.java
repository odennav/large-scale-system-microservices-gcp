//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.common.security;

import com.ntw.common.entity.UserAuth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by anurag on 25/03/17.
 */

/**
 * JJwtUtility is a helper class that uses JJwt library for creating and parsing access tokens
 */
public class JJwtUtility implements JwtUtility {

    private String secret = "123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims.
     * These properties are taken from the specified
     * User object. Tokens validity is infinite.
     *
     * @param userAuth the user for which the token will be generated
     * @return the JWT token
     */
    public String generateToken(UserAuth userAuth) {
        Claims claims = Jwts.claims().setSubject(userAuth.getName());
        claims.put("id", userAuth.getId() + "");
        claims.put("role", getListAsString(userAuth.getRoles()));
        claims.put("emailId", userAuth.getEmailId());

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * Tries to parse specified String as a JWT token. If successful, returns User object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the User object extracted from specified token or null if a token is invalid.
     */
    public UserAuth parseToken(String token) {
        try {
            Claims body = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            UserAuth u = new UserAuth();
            u.setName(body.getSubject());
            u.setId((String)body.get("id"));
            u.setRoles(getListFromString((String)body.get("role")));
            u.setEmailId((String) body.get("emailId"));

            return u;

        } catch (JwtException | ClassCastException e) {
            return null;
        }
    }

    private static final String ROLE_ID_DELIMETER = " ";

    public String getListAsString(List<String> roles) {
        StringBuilder roleIds = new StringBuilder("");
        for (String role : roles) {
            roleIds.append(role.toString()+ROLE_ID_DELIMETER);
        }
        return roleIds.toString();
    }

    private List<String> getListFromString(String roleIds) {
        StringTokenizer roleIdTokenizer = new StringTokenizer(roleIds,ROLE_ID_DELIMETER);
        List<String> roles = new LinkedList<>();
        while(roleIdTokenizer.hasMoreTokens()) {
            String roleId = roleIdTokenizer.nextToken().trim();
            roles.add(roleId);
        }
        return roles;
    }


}