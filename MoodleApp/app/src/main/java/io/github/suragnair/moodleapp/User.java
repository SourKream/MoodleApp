package io.github.suragnair.moodleapp;

class User {
    int ID;
    String firstName;
    String lastName;
    String username;
    String email;
    String entryNo;

    public User (int id, String usern, String first, String last, String em, String entryno){
        ID = id;
        firstName = first;
        lastName = last;
        username = usern;
        email = em;
        entryNo = entryno;
    }
}
