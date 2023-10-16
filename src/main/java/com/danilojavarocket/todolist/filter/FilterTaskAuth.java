package com.danilojavarocket.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.danilojavarocket.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Aqui eu valido se a rota que estamos chamando precisa ou não passar pela validação de Auth
        var servletPath = request.getServletPath();
        if(servletPath.startsWith("/tasks")){

            // Pegar a autenticação (user + pass)
            // Traz a informação em BASE64 (encoded)
            var authorization = request.getHeader("Authorization");

            // Aqui estou removendo o "Basic" do encoded que veio no authorization acima
            // Substring pode receber um ou dois argumentos INT
            // Estou pegando a palavra Basic, pedindo para pegar o tamanho dela e dando um trim para remover o espaço
            // Poderia fazer assim substring(5). Funcionaria da mesma forma
            var authEncoded = authorization.substring("Basic".length()).trim();

            // Aqui estou decodando para Base64, o auth que vem encodado
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

            // Aqui estou transformando esse Base64 em string (vai retornar assim = userName:userPassword)
            var authString = new String(authDecoded);

            // Split separa essa string que retorna, em um array de duas posições
            // A primeira, é o userName
            // A segunda, é o userPassword
            // Acesso as duas posições da forma abaixo
            String[] credentials = authString.split(":");
            String userName = credentials[0];
            String userPassword = credentials[1];

            // Aqui valido se o usuário está cadastrado no banco. Se não estiver, vou dar um 401
            var user = userRepository.findByUserName(userName);
            if (user == null){
                response.sendError(401);
            }else {
                // TODO - Alterar a ordem desse If/Else
                // Aqui, se ele estiver cadastrado, sigo para a validação da senha
                var passwordVerify = BCrypt.verifyer().verify(userPassword.toCharArray(), user.getPassword());
                if (passwordVerify.verified){
                    request.setAttribute("idUser",user.getId());
                    filterChain.doFilter(request,response);

                }else {
                    response.sendError(401);
                }
            }
        }else {
            filterChain.doFilter(request,response);
        }
    }
}
