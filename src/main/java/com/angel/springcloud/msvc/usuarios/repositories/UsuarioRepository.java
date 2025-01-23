package com.angel.springcloud.msvc.usuarios.repositories;

import com.angel.springcloud.msvc.usuarios.entities.Usuario;
import org.springframework.data.repository.CrudRepository;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
}
