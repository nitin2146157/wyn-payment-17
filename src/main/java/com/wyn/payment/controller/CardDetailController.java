package com.wyn.payment.controller;

import com.wyn.payment.entity.*;
import com.wyn.payment.exception.CardDetailNotFound;
import com.wyn.payment.repository.*;
import com.wyn.payment.service.CardDetailService;
import com.wyn.payment.service.CardTypeService;
import com.wyn.payment.service.ClientDetailService;
import com.wyn.payment.service.TransactionService;
import com.wyn.payment.serviceImpl.TransactionServiceImpl;
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
    private CardDetailRepository cardDetailRepository;

    @Autowired
    private ClientDetailService clientDetailService;

    @Autowired
    private ClientDetailRepository clientDetailRepository;

    @Autowired
    private CardTypeService cardTypeService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionInfoRepository repo;

    @Autowired
    private TransactionServiceImpl transactionServiceImpl;

    @Autowired
    private CardTypeRepository cardTypeRepository;

    @Value("${EXPIRE_DAYS}")
    private String expireDays;

    @Value("${EXPIRE_DAYS_SPLIT}")
    private String expireDaysSplit;

    @GetMapping("/create")
    public ResponseEntity<?> createCardDetailPage(@RequestParam("HK") String hashKey) {
        TransactionInfo transactionInfo = transactionService.findIfClientIsAuthenticated(hashKey);

        if (transactionInfo != null) {
            CardDetail cardDetail = new CardDetail();
            cardDetail.setTransactionInfo(transactionInfo);
            return ResponseEntity.ok(cardDetail);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createCardDetail(@Valid @RequestBody CardDetail cardDetail, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            response.put("cardDetail", cardDetail);
            response.put("currentYear", Calendar.getInstance().getWeekYear());
            response.put("cardTypeList", cardTypeService.findAll());
            return ResponseEntity.badRequest().body(response);
        }
        cardDetail.setTransactionInfo(transactionService.findById(cardDetail.getTransactionInfo().getId()).get());
        cardDetail.setCreatedTs(Calendar.getInstance().getTime());
        cardDetail.setModifiedTs(Calendar.getInstance().getTime());
        cardDetail.setActive(true);
        LOGGER.info("Creating card detail");
        CardDetail createdCardDetail = cardDetailService.create(cardDetail);
        if (createdCardDetail == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("cardDetail", cardDetail);
            response.put("currentYear", Calendar.getInstance().getWeekYear());
            response.put("cardTypeList", cardTypeService.findAll());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        LOGGER.info("Card detail created successfully, updating transaction info");
        TransactionInfo transactionInfo = cardDetail.getTransactionInfo();
        transactionInfo.setTransactionStatus(
                transactionService.findByTransactionStatusName(TransactionStatusRepository.Status.PENDING.toString()));
        transactionService.updateTransactionInfo(transactionInfo);
        LOGGER.info("Transaction info updated, returning response");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCardDetail);
    }

    // @PostMapping("/create")
    // public ResponseEntity<?> createCardDetail(@Valid @RequestBody CardDetail
    // cardDetail) {
    // // if (result.hasErrors()) {
    // // return ResponseEntity.badRequest().body(result.getAllErrors());
    // // }

    // CardType type = cardTypeRepository.save(cardDetail.getCardType());
    // TransactionInfo createTransactionInfo =
    // transactionServiceImpl.createObject(cardDetail.getTransactionInfo());

    // Optional<TransactionInfo> transactionInfoOpt =
    // transactionService.findById(createTransactionInfo.getId());
    // if (!transactionInfoOpt.isPresent()) {
    // return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction info not
    // found");
    // }

    // cardDetail.setTransactionInfo(transactionInfoOpt.get());
    // cardDetail.setCreatedTs(Calendar.getInstance().getTime());
    // cardDetail.setModifiedTs(Calendar.getInstance().getTime());
    // cardDetail.setActive(true);

    // CardDetail createdCardDetail = cardDetailService.create(cardDetail);

    // if (createdCardDetail == null) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed
    // to create card detail");
    // }

    // // TransactionInfo transactionInfo = cardDetail.getTransactionInfo();
    // //
    // transactionInfo.setTransactionStatus(transactionService.findByTransactionStatusName(TransactionStatusRepository.Status.PENDING.toString()));
    // // transactionService.updateTransactionInfo(transactionInfo);

    // return ResponseEntity.status(HttpStatus.CREATED).body(createdCardDetail);

    // // if(cardDetail.getTransactionInfo()!=null){
    // // TransactionInfo transactionInfo = cardDetail.getTransactionInfo();
    // //
    // // if(transactionInfo.getClientDetail()!=null){
    // //
    // transactionInfo.setClientDetail(clientDetailRepository.save(transactionInfo.getClientDetail()));
    // // }
    // //
    // // cardDetail.setTransactionInfo(repo.save(transactionInfo));
    // // }
    // //
    // // CardDetail savedCardDetail = cardDetailRepository.save(cardDetail);
    // //
    // // return ResponseEntity.ok(savedCardDetail);
    // }

    @GetMapping("/list")
    public ResponseEntity<List<CardDetail>> cardDetailListPage(@RequestParam(required = false) String referenceNo) {
        List<CardDetail> cardDetailList = null;
        if (!Gentools.isEmptyString(referenceNo)) {
            cardDetailList = cardDetailService.findByReferenceNo(referenceNo);
        }
        LOGGER.info("hlljd");
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
                "transactionStatusList", transactionService.findByTransactionStatusIdIn(Arrays.asList(2, 3, 4))));
    }

    @PostMapping("/{clientId}/edit/{id}")
    public ResponseEntity<?> editCardDetail(@RequestBody CardDetail updatedCardDetail, @PathVariable Integer id,
            @PathVariable Integer clientId, @RequestParam(required = false) String redact) {
        Optional<ClientDetail> clientDetail = clientDetailService.findById(clientId);

        if (!clientDetail.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found");
        }

        CardDetail cardDetail = cardDetailService.findById(clientDetail, id);
        if (cardDetail == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card detail not found");
        }

        TransactionInfo updatedTransactionInfo = updatedCardDetail.getTransactionInfo();
        if (updatedTransactionInfo != null) {
            Optional<TransactionStatus> transactionStatusOpt = transactionService
                    .findByTransactionStatusId(updatedTransactionInfo.getTransactionStatus().getId());
            if (transactionStatusOpt.isPresent()) {
                TransactionInfo transactionInfo = cardDetail.getTransactionInfo();
                if (transactionInfo != null) {
                    transactionInfo.setTransactionStatus(transactionStatusOpt.get());
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Transaction info not found in card detail");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction status not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Updated transaction info is null");
        }
        // cardDetail.getTransactionInfo().setTransactionStatus(transactionService.findByTransactionStatusId(updatedCardDetail.getTransactionInfo().getTransactionStatus().getId()));
        try {
            if ("Y".equalsIgnoreCase(redact)) {
                cardDetailService.expireCard(cardDetail);
            } else {
                cardDetailService.update(cardDetail);
            }
            return ResponseEntity.ok("Card status was successfully updated");
        } catch (CardDetailNotFound e) {
            LOGGER.info("There was an error updating card status. Card not found >" + e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("There was an error updating card status. Card not found");
        }
    }

    @GetMapping("/expire")
    public ResponseEntity<?> expireOldCards() {
        cardDetailService.expireOldCardsbyDays(Gentools.parseInt(expireDays), Gentools.parseInt(expireDaysSplit));
        return ResponseEntity.ok("Old cards expired successfully");
    }

    // @GetMapping("/test")
    // public String testpost(){
    //
    // return "test succesful";
    // }
    // @GetMapping("/test2")
    // public String testpost2(){
    //
    // return "test succesful";
    // }
}
