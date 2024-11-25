package ru.practicum.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.comments.dto.NewCommentDto;
import ru.practicum.comments.dto.UpdateCommentDto;
import ru.practicum.comments.dto.UserCommentDto;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.repository.CommentRepository;
import ru.practicum.events.model.Event;
import ru.practicum.events.repository.EventRepository;
import ru.practicum.exception.NotFoundException;
//import ru.practicum.exception.ViolationException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;

import static ru.practicum.events.model.State.PUBLISHED;

@Service
@RequiredArgsConstructor
public class PrivateCommentServiceImpl implements PrivateCommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Override
    public UserCommentDto userAddComment(NewCommentDto newCommentDto, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + authorId + " не найден"));

        Long eventId = newCommentDto.getEventId();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не найдено"));

        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " не опубликовано");
        }

        Comment comment = commentRepository.save(commentMapper.newCommentDtoToComment(newCommentDto, author, event));

        return commentMapper.commentToUserCommentDto(comment);
    }

    @Override
    public UserCommentDto userUpdateComment(UpdateCommentDto updateCommentDto, Long authorId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new NotFoundException("Только создатель комментария может его изменять"));

        if (updateCommentDto.getText() != null) {
            comment.setText(updateCommentDto.getText());
        }
        comment.setUpdatedOn(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.commentToUserCommentDto(updatedComment);
    }

    @Override
    public void userDeleteComment(Long authorId, Long commentId) {
        Comment comment = commentRepository.findByIdAndAuthorId(commentId, authorId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id: " + commentId + " не найден или не принадлежит пользователю с id: " + authorId));

        commentRepository.deleteById(commentId);
    }
}