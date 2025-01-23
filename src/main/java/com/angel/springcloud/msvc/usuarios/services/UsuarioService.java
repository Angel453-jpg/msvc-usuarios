package com.angel.springcloud.msvc.usuarios.services;

import com.angel.springcloud.msvc.usuarios.entities.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {

    List<Usuario> listar();

    Optional<Usuario> obtenerPorId(Long id);

    Usuario guardar(Usuario usuario);

    void eliminar(Long id);

}
