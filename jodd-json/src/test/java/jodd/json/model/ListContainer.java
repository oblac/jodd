// Copyright (c) 2003-present, Jodd Team (jodd.org). All Rights Reserved.

package jodd.json.model;

import java.util.List;

public class ListContainer {

    private String name;
    private List people;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List getPeople() {
        return people;
    }

    public void setPeople(List people) {
        this.people = people;
    }
}
