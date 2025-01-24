package com.angel.springcloud.msvc.usuarios.controllers;

import com.angel.springcloud.msvc.usuarios.entities.Usuario;
import com.angel.springcloud.msvc.usuarios.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController("/api/v1/usuarios")
@Tag(name = "Usuarios", description = "API para gestionar los usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Devuelve una lista con todos los usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public List<Usuario> listar() {
        return usuarioService.listar();
    }

    @Operation(summary = "Obtener un usuario por su ID", description = "Devuelve un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.obtenerPorId(id);
        if (usuarioOptional.isPresent()) {
            return ResponseEntity.ok().body(usuarioOptional.get());
        }

        return ResponseEntity.notFound().build();

    }

    @Operation(summary = "Crear un nuevo usuario", description = "Crea un nuevo usuario en la base de datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida")
    })
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuario));
    }

    @Operation(summary = "Actualizar usuario", description = "Permite actualizar los datos de un usuario existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@RequestBody Usuario usuario, @PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.obtenerPorId(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioDb = usuarioOptional.get();
            usuarioDb.setNombre(usuario.getNombre());
            usuarioDb.setEmail(usuario.getEmail());
            usuarioDb.setPassword(usuario.getPassword());

            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuarioDb));
        }

        return ResponseEntity.notFound().build();

    }

    @Operation(summary = "Eliminar un usuario", description = "Elimina un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {

        Optional<Usuario> usuarioOptional = usuarioService.obtenerPorId(id);
        if (usuarioOptional.isPresent()) {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }

}
