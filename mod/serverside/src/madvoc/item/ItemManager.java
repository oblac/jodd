// Copyright (c) 2003-2012, Jodd Team (jodd.org). All Rights Reserved.

package madvoc.item;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.SessionScope;

import java.util.ArrayList;
import java.util.List;

/**
 * Scoped person manager.
 */
@PetiteBean(scope=SessionScope.class)
public class ItemManager {

	private int idCount;

	private List<Item> items = new ArrayList<Item>();

    public void add(Item item) {
		item.setId(idCount++);
		items.add(item);
    }

    public List<Item> getAllItems() {
        return items;
    }
}
