package com.zfb.current.controller;

import com.zfb.current.service.CurrentAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/current/accounts")
@RequiredArgsConstructor
public class CurrentAccountController {

  private final CurrentAccountService accountService;
}
