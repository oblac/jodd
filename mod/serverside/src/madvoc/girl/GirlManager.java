// Copyright (c) 2003-2009, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.girl;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.SessionScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Scoped person manager.
 */
@PetiteBean(scope=SessionScope.class)
public class GirlManager {

	private int idCount;

	private List<Girl> girls = new ArrayList<Girl>();

    public void add(Girl girl) {
		girl.setId(idCount++);
		girls.add(girl);
    }

    public List<Girl> getAllGirls() {
        return girls;
    }
}
