package org.brain.springtelegramai.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.brain.springtelegramai.model.ChatEntity;
import org.brain.springtelegramai.model.MessageEntity;
import org.brain.springtelegramai.payload.response.ChatResponse;
import org.brain.springtelegramai.payload.response.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin service", description = "Admin console API")
@RequestMapping("/api/v1/console")
@Validated
public interface AdminConsoleApi {
    @Operation(summary = "Get all chats")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = ChatEntity.class)), mediaType = "application/json")})})
    @GetMapping(value = "/chat")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<ChatResponse>> getAllChats();

    @Operation(summary = "Get chat messages")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(array = @ArraySchema(schema = @Schema(implementation = MessageEntity.class)), mediaType = "application/json")})})
    @GetMapping(value = "/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<List<MessageResponse>> getChatMessages(@PathVariable Long chatId);

    @Operation(summary = "Send message to chat")
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {
                    @Content(schema = @Schema(implementation = MessageEntity.class), mediaType = "application/json")})})
    @PostMapping(value = "/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<Void> sendMessageToChat(@PathVariable Long chatId, @RequestBody String message);
}
