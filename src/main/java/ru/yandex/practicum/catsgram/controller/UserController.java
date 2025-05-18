package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.catsgram.model.User;

import java.util.HashMap;

@RestController
@RequestMapping
public class UserController {

    private final HashMap<Long, User> users = new HashMap<>();
}
