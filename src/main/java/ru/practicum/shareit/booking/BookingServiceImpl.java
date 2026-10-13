package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingInputDto inputDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item item = itemRepository.findById(inputDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id " + inputDto.getItemId() + " не найдена"));

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может забронировать собственную вещь");
        }

        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new ValidationException("Вещь с id " + item.getId() + " недоступна для бронирования");
        }

        validateBookingDates(inputDto.getStart(), inputDto.getEnd());

        Booking booking = Booking.builder()
                .start(inputDto.getStart())
                .end(inputDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Создано бронирование с id {} пользователем с id {}", savedBooking.getId(), userId);
        return BookingMapper.toBookingDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ValidationException(
                    "Пользователь с id " + userId + " не является владельцем вещи для бронирования " + bookingId);
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования с id " + bookingId + " уже был изменен");
        }

        if (Boolean.TRUE.equals(approved)) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Статус бронирования с id {} изменен на {}", bookingId, updatedBooking.getStatus());
        return BookingMapper.toBookingDto(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getById(Long userId, Long bookingId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        boolean isBooker = booking.getBooker().getId().equals(userId);
        boolean isOwner = booking.getItem().getOwner().getId().equals(userId);

        if (!isBooker && !isOwner) {
            throw new NotFoundException(
                    "Пользователь с id " + userId + " не имеет доступа к просмотру бронирования " + bookingId);
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByBooker(Long userId, String stateStr) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwner(Long userId, String stateStr) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        List<Item> items = itemRepository.findByOwnerIdOrderByIdAsc(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        BookingState state = BookingState.from(stateStr);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                        userId, now, now);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, now);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, now);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(
                        userId, BookingStatus.REJECTED);
                break;
            case ALL:
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void validateBookingDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new ValidationException("Даты бронирования не могут быть пустыми");
        }
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new ValidationException("Дата начала не может быть позже или равна дате окончания");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }
    }
}