package com.angel.springcloud.msvc.usuarios.controllers;

import com.angel.springcloud.msvc.usuarios.controllers.examples.UsuarioExamples;
import com.angel.springcloud.msvc.usuarios.entities.Usuario;
import com.angel.springcloud.msvc.usuarios.services.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuario obtenido correctamente",
                    content = @Content(
                            schema = @Schema(implementation = Usuario.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta 200",
                                    value = UsuarioExamples.USUARIO_OBTENIDO
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuario no encontrado",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de respuesta 404",
                                    value = UsuarioExamples.USUARIO_NO_ENCONTRADO
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> detalle(@PathVariable Long id) {

        Optional<Usuario> usuarioOptional = getUsuario(id);
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
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario, BindingResult result) {

        if (result.hasErrors()) {
            return validar(result);
        }

        if (!usuario.getEmail().isEmpty() && usuarioService.existePorEmail(usuario.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(Collections
                            .singletonMap("mensaje", "El email ya está registrado con ese correo electrónico"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.guardar(usuario));
    }

    @Operation(summary = "Actualizar usuario", description = "Permite actualizar los datos de un usuario existente por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @RequestBody Usuario usuario, BindingResult result, @PathVariable Long id) {

        if (result.hasErrors()) {
            return validar(result);
        }

        Optional<Usuario> usuarioOptional = getUsuario(id);
        if (usuarioOptional.isPresent()) {
            Usuario usuarioDb = usuarioOptional.get();

            if (!usuario.getEmail().isEmpty()
                    && !usuario.getEmail().equalsIgnoreCase(usuarioDb.getEmail())
                    && usuarioService.obtenerPorEmail(usuario.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Collections
                                .singletonMap("mensaje", "El email ya está registrado con ese correo electrónico"));
            }

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

        Optional<Usuario> usuarioOptional = getUsuario(id);
        if (usuarioOptional.isPresent()) {
            usuarioService.eliminar(id);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();

    }

    @GetMapping("/usuarios-por-curso")
    @Operation(summary = "Obtener todos los usuarios por curso", description = "Devuelve una lista de usuarios por curso")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerUsuariosPorCurso(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(usuarioService.listarPorIds(ids));
    }

    private static ResponseEntity<Map<String, String>> validar(BindingResult result) {
        Map<String, String> errores = new HashMap<>();
        result.getFieldErrors().forEach(
                error -> errores.put(error.getField(), "El campo " + error.getField() + " " + error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errores);
    }

    private Optional<Usuario> getUsuario(Long id) {
        return usuarioService.obtenerPorId(id);
    }

}
