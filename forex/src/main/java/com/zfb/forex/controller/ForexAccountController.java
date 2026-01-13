package com.zfb.forex.controller;

import com.zfb.forex.service.ForexAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/forex/accounts")
@RequiredArgsConstructor
public class ForexAccountController {

  private final ForexAccountService accountService;
}
