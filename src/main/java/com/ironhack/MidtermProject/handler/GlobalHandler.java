package com.ironhack.MidtermProject.handler;

import com.ironhack.MidtermProject.exceptions.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(CurrencyTypeException.class)
    public void currencyTypeExceptionHandler(CurrencyTypeException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(FraudException.class)
    public void fraudExceptionHandler(FraudException noSuchSalesRepException, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, noSuchSalesRepException.getMessage());
    }

    @ExceptionHandler(IdNotFoundException.class)
    public void idNotFoundExceptionHandler(IdNotFoundException noSuchOpportunityException, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, noSuchOpportunityException.getMessage());
    }

    @ExceptionHandler(NoHeaderException.class)
    public void noHeaderExceptionHandler(NoHeaderException opportunityAlreadyClosedException, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, opportunityAlreadyClosedException.getMessage());
    }

    @ExceptionHandler(NoOwnerException.class)
    public void noOwnerExceptionHandler(NoOwnerException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public void notEnoughMoneyExceptionHandler(NotEnoughMoneyException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
    }

    @ExceptionHandler(StatusException.class)
    public void statusExceptionHandler(StatusException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public void userAlreadyExistExceptionHandler(UserAlreadyExistException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
    }
}

