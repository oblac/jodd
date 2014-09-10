// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.action.tag;

import jodd.bean.BeanCopy;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.RestAction;

@MadvocAction("/tag")
public class TagAction {

	@In
	Long id;

	@In
	Tag tag;

	// simulate service call
	private Tag loadTagById(Long id) {
		Tag tag = new Tag();
		tag.setTagId(id);
		tag.setName("jodd");
		return tag;
	}

	// applies changes from source to destination
	private void apply(Tag source, Tag dest) {
		BeanCopy.beans(source, dest).ignoreNulls(true).copy();
	}

	@RestAction(value = "disable/${id}")
	public String disable() {
		tag = loadTagById(id);
		return "text:disable-" + tag;
	}

	// if we want to use 'delete' with @RestAction and GET
	@RestAction(value = "delete/${id}", method = Action.GET)
	public String delete() {
		tag = loadTagById(id);
		return "text:delete-" + tag;
	}

	@RestAction(value = "${id}")
	//@RestAction(value = "edit/${id}")
	public String edit() {
		Tag oldTag = loadTagById(id);

		apply(tag, oldTag);

		tag = oldTag;

		return "text:edit-" + tag;
	}

	@RestAction(value = "${id}")
	public String save() {
		tag.setTagId(id);

		// save tag

		return "text:save-" + tag;
	}
}