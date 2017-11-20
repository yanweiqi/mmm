package com.mex.bidder.adx.gy;

import com.mex.bidder.tutorial.AddressBookProtos;

import java.io.*;

/**
 * Created by Administrator on 2016/12/22.
 */
public class AddPerson {

    static AddressBookProtos.Person PromptForAddress(BufferedReader stdin, PrintStream stdout)
            throws IOException {
        AddressBookProtos.Person.Builder person = AddressBookProtos.Person.newBuilder();

        stdout.print("Enter person ID: ");
        person.setId(Integer.valueOf(stdin.readLine()));

        stdout.print("Enter name: ");
        person.setName(stdin.readLine());

        stdout.print("Enter email address (blank for none): ");
        String email = stdin.readLine();
        if (email.length() > 0) {
            person.setEmail(email);
        }

        while (true) {
            stdout.print("Enter a phone number (or leave blank to finish): ");
            String number = stdin.readLine();
            if (number.length() == 0) {
                break;
            }

            AddressBookProtos.Person.PhoneNumber.Builder phoneNumber = AddressBookProtos.Person.PhoneNumber
                    .newBuilder().setNumber(number);

            stdout.print("Is this a mobile, home, or work phone? ");
            String type = stdin.readLine();
            if (type.equals("mobile")) {
                phoneNumber.setType(AddressBookProtos.Person.PhoneType.MOBILE);
            } else if (type.equals("home")) {
                phoneNumber.setType(AddressBookProtos.Person.PhoneType.HOME);
            } else if (type.equals("work")) {
                phoneNumber.setType(AddressBookProtos.Person.PhoneType.WORK);
            } else {
                stdout.println("Unknown phone type.  Using default.");
            }

            person.addPhone(phoneNumber);
        }

        return person.build();
    }

    // Main function: Reads the entire address book from a file,
    // adds one person based on user input, then writes it back out to the same
    // file.
    public static void main(String[] args) throws Exception {
     /*   if (args.length != 1) {
            System.err.println("Usage:  AddPerson ADDRESS_BOOK_FILE");
            System.exit(-1);
        }*/
        args = new String[5];
     args[0] = "1";
     args[1] = "xiaoming";
     args[2] = "mex@123.com";
     args[3] = "1851003232";
     args[4] = "1";

        AddressBookProtos.AddressBook.Builder addressBook = AddressBookProtos.AddressBook.newBuilder();

        // Read the existing address book.
        try {
            addressBook.mergeFrom(new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println(args[0]
                    + ": File not found.  Creating a new file.");
        }

        // Add an address.
        addressBook.addPerson(PromptForAddress(new BufferedReader(
                new InputStreamReader(System.in)), System.out));

        // Write the new address book back to disk.
        FileOutputStream output = new FileOutputStream(args[0]);
        addressBook.build().writeTo(output);
        output.close();
    }
}
