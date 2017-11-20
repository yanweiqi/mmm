package com.mex.bidder.adx.gy;

import com.mex.bidder.tutorial.AddressBookProtos;

import java.io.FileInputStream;

/**
 * Created by Administrator on 2016/12/22.
 */
public class ListPerson {

    // Iterates though all people in the AddressBook and prints info about them.
    static void Print(AddressBookProtos.AddressBook addressBook) {
        for (AddressBookProtos.Person person: addressBook.getPersonList()) {
            System.out.println("Person ID: " + person.getId());
            System.out.println("  Name: " + person.getName());
            if (person.hasEmail()) {
                System.out.println("  E-mail address: " + person.getEmail());
            }

            for (AddressBookProtos.Person.PhoneNumber phoneNumber : person.getPhoneList()) {
                switch (phoneNumber.getType()) {
                    case MOBILE:
                        System.out.print("  Mobile phone #: ");
                        break;
                    case HOME:
                        System.out.print("  Home phone #: ");
                        break;
                    case WORK:
                        System.out.print("  Work phone #: ");
                        break;
                }
                System.out.println(phoneNumber.getNumber());
            }
        }
    }

    // Main function:  Reads the entire address book from a file and prints all
    //   the information inside.
    public static void main(String[] args) throws Exception {
      /*  if (args.length != 1) {
            System.err.println("Usage:  ListPeople ADDRESS_BOOK_FILE");
            System.exit(-1);
        }*/
        args = new String[5];

        // Read the existing address book.
        AddressBookProtos.AddressBook addressBook =
                AddressBookProtos.AddressBook.parseFrom(new FileInputStream(args[0]));

        Print(addressBook);
    }
}
