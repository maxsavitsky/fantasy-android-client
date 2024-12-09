package by.bsu.chgkfantasyclient.entity;

import java.util.ArrayList;

import lombok.Data;

@Data
public class User {

    private final long id;
    private final String username;
    private final String name;
    private final ArrayList<Long> pick_ids;
}
