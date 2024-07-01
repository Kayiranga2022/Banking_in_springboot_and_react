package com.banking.service.Impl;

import com.banking.dto.EmailDetails;
import com.banking.entity.Transaction;
import com.banking.entity.User;
import com.banking.repository.TransactionRepository;
import com.banking.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {

    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    private static final String FILE = "C:\\Users\\nesta\\Documents\\MyStatement.pdf";

    // display transactions of a given account in a given date range

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> !transaction.getCreatedAt().toLocalDate().isBefore(start) && !transaction.getCreatedAt().toLocalDate().isAfter(end))
                .toList();

        User user = userRepository.findByAccountNumber( accountNumber );
        String customerName = user.getFirstName() + " " + user.getLastName() + " " + user.getOtherName();

        Rectangle statementSize = new Rectangle( PageSize.A4 );
        Document document = new Document(statementSize);
        log.info("setting size of the document");
        OutputStream outputStream=new FileOutputStream( FILE );
        PdfWriter.getInstance( document, outputStream );
        document.open();

        PdfPTable bankInfoTable = new PdfPTable( 1 );
        PdfPCell bankName = new PdfPCell(new Phrase("THE HUSTLERS BANK"));
        bankName.setBorder( 0 );
        bankName.setBackgroundColor( BaseColor.BLUE );
        bankName.setPadding( 20f );

        PdfPCell bankAddress = new PdfPCell(new Phrase("KG107AVG, In Kigali Rwanda"));
        bankAddress.setBorder( 0 );
        bankInfoTable.addCell( bankName );
        bankInfoTable.addCell( bankAddress );

        PdfPTable statementInfo = new PdfPTable( 2 );
        PdfPCell customerInfo = new PdfPCell(new Phrase("start Date: "+ startDate));
        customerInfo.setBorder( 0 );
        PdfPCell statement = new PdfPCell(new Phrase("STATEMENT OF ACCOUNT"));
        statement.setBorder( 0 );
        PdfPCell stopDate = new PdfPCell(new Phrase("endDate: "+ endDate));
        stopDate.setBorder( 0 );
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
        name.setBorder( 0 );
        PdfPCell space = new PdfPCell();
        PdfPCell address = new PdfPCell(new Phrase("Customer Adddress: " + user.getAddress()));
        address.setBorder( 0 );

        PdfPTable transactionsTable = new  PdfPTable( 4 );
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor( BaseColor.BLUE );
        date.setBorder( 0 );
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor( BaseColor.BLUE );
        transactionType.setBorder( 0 );
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBackgroundColor( BaseColor.BLUE );
        transactionAmount.setBorder( 0 );
       PdfPCell status = new PdfPCell(new Phrase("STATUS"));
       status.setBackgroundColor( BaseColor.BLUE );
       status.setBorder( 0 );

       transactionsTable.addCell( date );
       transactionsTable.addCell( transactionType );
       transactionsTable.addCell( transactionAmount );
       transactionsTable.addCell( status );

       transactionList.forEach(transaction -> {
           transactionsTable.addCell( new Phrase(transaction.getCreatedAt().toString()) );
          transactionsTable.addCell( new Phrase(transaction.getTransactionType()) );
          transactionsTable.addCell( new Phrase(transaction.getAmount().toString()) );
          transactionsTable.addCell( new Phrase(transaction.getStatus()) );
       });

       statementInfo.addCell( customerInfo );
       statementInfo.addCell( statement );
       statementInfo.addCell( endDate );
       statementInfo.addCell( name );
       statementInfo.addCell( space );
       statementInfo.addCell( address );

       document.add( bankInfoTable );
       document.add( statementInfo );
       document.add( transactionsTable );

       document.close();

        EmailDetails emailDetails= EmailDetails.builder()
                .recipient( user.getEmail() )
                .subject( "STATEMENT OF YOUR ACCOUNT" )
                .messageBody( "Kindly Find Your requested account statement attached!" )
                .attachment( FILE )
                .build();
       emailService.sendEmailWithAttachment( emailDetails );
        return transactionList;
    }

}
