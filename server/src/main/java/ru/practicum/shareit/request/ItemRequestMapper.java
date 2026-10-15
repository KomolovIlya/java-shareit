package ru.practicum.shareit.request;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
        if (dto == null) {
            return null;
        }
        return ItemRequest.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<Item> items) {
        if (request == null) {
            return null;
        }
        List<ItemRequestDto.ItemAnswerDto> answerDtos = items == null ? Collections.emptyList() :
                items.stream()
                        .map(item -> ItemRequestDto.ItemAnswerDto.builder()
                                .id(item.getId())
                                .name(item.getName())
                                .ownerId(item.getOwner() != null ? item.getOwner().getId() : null)
                                .build())
                        .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .items(answerDtos)
                .build();
    }
}