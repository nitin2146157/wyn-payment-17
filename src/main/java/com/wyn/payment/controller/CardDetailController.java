package com.wyn.payment.controller;

import com.wyn.payment.entity.CardDetail;
import com.wyn.payment.entity.ClientDetail;
import com.wyn.payment.entity.TransactionInfo;
import com.wyn.payment.exception.CardDetailNotFound;
import com.wyn.payment.repository.TransactionStatusRepository;
import com.wyn.payment.service.CardDetailService;
import com.wyn.payment.service.CardTypeService;
import com.wyn.payment.service.ClientDetailService;
import com.wyn.payment.service.TransactionService;
import com.wyn.payment.util.Gentools;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.wyn.payment.util.Gentools.daysBetween;
@RestController
@RequestMapping("/card")
public class CardDetailController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardDetailController.class);

    @Autowired
    private CardDetailService cardDetailService;

    @Autowired
    private ClientDetailService clientDetailService;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private TransactionService transactionService;

    @Value("${EXPIRE_DAYS}")
    private String expireDays;

    @Value("${EXPIRE_DAYS_SPLIT}")
    private String expireDaysSplit;

    @GetMapping("/create")
    public ResponseEntity<?> createCardDetailPage(@RequestParam("HK") String hashKey) {
        TransactionInfo transactionInfo = transactionService.findIfClientIsAuthenticated(hashKey);

        if (transactionInfo != null) {
            CardDetail cardDetail = new CardDetail();
            cardDetail.setTransactionInfo(Optional.of(transactionInfo));
            return ResponseEntity.ok(cardDetail);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCardDetail(@Valid @RequestBody CardDetail cardDetail, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        cardDetail.setTransactionInfo(transactionService.findById(cardDetail.getTransactionInfo().getId()));
        cardDetail.setCreatedTs(Calendar.getInstance().getTime());
        cardDetail.setModifiedTs(Calendar.getInstance().getTime());
        cardDetail.setActive(true);

        CardDetail createdCardDetail = cardDetailService.create(cardDetail);

        if (createdCardDetail == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create card detail");
        }

        TransactionInfo transactionInfo = cardDetail.getTransactionInfo();
        transactionInfo.setTransactionStatus(transactionService.findByTransactionStatusName(TransactionStatusRepository.Status.PENDING.toString()));
        transactionService.updateTransactionInfo(transactionInfo);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdCardDetail);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CardDetail>> cardDetailListPage(@RequestParam(required = false) String referenceNo) {
        List<CardDetail> cardDetailList = null;
        if (!Gentools.isEmptyString(referenceNo)) {
            cardDetailList = cardDetailService.findByReferenceNo(referenceNo);
        }
        return ResponseEntity.ok(cardDetailList);
    }

    @GetMapping("/{clientId}/edit/{id}")
    public ResponseEntity<?> editCardDetailPage(@PathVariable Integer id, @PathVariable Integer clientId) {
        Optional<ClientDetail> clientDetail = clientDetailService.findById(clientId);

        if (!clientDetail.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        CardDetail cardDetail = cardDetailService.findById(clientDetail, id);

        if (cardDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card detail not found");
        }

        Calendar createdTs = Calendar.getInstance();
        createdTs.setTime(cardDetail.getTransactionInfo().getCreatedTs());

        if (cardDetail.isSplit()) {
            createdTs.add(Calendar.DAY_OF_MONTH, Gentools.parseInt(expireDaysSplit));
        } else {
            createdTs.add(Calendar.DAY_OF_MONTH, Gentools.parseInt(expireDays));
        }

        int daysToExpiry = daysBetween(Calendar.getInstance().getTime(), createdTs.getTime());

        return ResponseEntity.ok(Map.of(
                "cardDetail", cardDetail,
                "daysToExpiry", ++daysToExpiry + " day(s)",
                "cardTypeList", cardTypeService.findAll(),
                "transactionStatusList", transactionService.findByTransactionStatusIdIn(Arrays.asList(2, 3, 4))
        ));
    }

    @PostMapping("/{clientId}/edit/{id}")
    public ResponseEntity<?> editCardDetail(@RequestBody CardDetail updatedCardDetail, @PathVariable Integer id, @PathVariable Integer clientId, @RequestParam(required = false) String redact) {
        Optional<ClientDetail> clientDetail = clientDetailService.findById(clientId);

        if (!clientDetail.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        CardDetail cardDetail = cardDetailService.findById(clientDetail, id);
        if (cardDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card detail not found");
        }

        cardDetail.getTransactionInfo().setTransactionStatus(transactionService.findByTransactionStatusId(updatedCardDetail.getTransactionInfo().getTransactionStatus().getId()));
        try {
            if ("Y".equalsIgnoreCase(redact)) {
                cardDetailService.expireCard(cardDetail);
            } else {
                cardDetailService.update(cardDetail);
            }
            return ResponseEntity.ok("Card status was successfully updated");
        } catch (CardDetailNotFound e) {
            LOGGER.info("There was an error updating card status. Card not found >" + e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("There was an error updating card status. Card not found");
        }
    }

    @GetMapping("/expire")
    public ResponseEntity<?> expireOldCards() {
        cardDetailService.expireOldCardsbyDays(Gentools.parseInt(expireDays), Gentools.parseInt(expireDaysSplit));
        return ResponseEntity.ok("Old cards expired successfully");
    }
}
