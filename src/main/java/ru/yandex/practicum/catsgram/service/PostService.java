package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.Post;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostService {
    private final UserService userService;
    private final Map<Long, Post> posts = new HashMap<>();

    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll(int size, String sort, int from) {
        Stream<Post> stream = posts.values().stream();

        if (SortOrder.from(sort) == SortOrder.ASCENDING) {
            stream = stream.sorted(Comparator.comparing(Post::getPostDate));
        } else {
            stream = stream.sorted(Comparator.comparing(Post::getPostDate).reversed());
        }

        return stream
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    public Optional<Post> findPostById(Long id) {
        return Optional.ofNullable(posts.get(id));
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank()) {
            throw new ConditionsNotMetException("Описание не может быть пустым");
        }
        if (userService.findUserById(post.getAuthorId()).isEmpty()) {
            throw new ConditionsNotMetException("Автор с id = " + post.getAuthorId() + " не найден");
        }

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}