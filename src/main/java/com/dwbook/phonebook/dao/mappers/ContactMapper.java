package com.dwbook.phonebook.dao.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import com.dwbook.phonebook.representations.Contact;

public class ContactMapper implements ResultSetMapper<Contact> {
    @Override
    public Contact map(int i, ResultSet rs, StatementContext sc) throws SQLException {
        return new Contact(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"), rs.getString("phone"));
    }
}
