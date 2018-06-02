// Copyright (c) 2003-present, Jodd Team (http://jodd.org)
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// 1. Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
//
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package jodd.madvoc.action.tag;

import jodd.bean.BeanCopy;
import jodd.madvoc.meta.method.GET;
import jodd.madvoc.meta.In;
import jodd.madvoc.meta.MadvocAction;
import jodd.madvoc.meta.RestAction;
import jodd.madvoc.result.TextResult;

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

	@RestAction(value = "disable/{id}")
	public TextResult disable() {
		tag = loadTagById(id);
		return TextResult.of("disable-" + tag);
	}

	// if we want to use 'delete' with @RestAction and GET
	@RestAction("delete/{id}")
	@GET
	public TextResult delete() {
		tag = loadTagById(id);
		return TextResult.of("delete-" + tag);
	}

	@RestAction(value = "{id}")
	//@RestAction(value = "edit/{id}")
	public TextResult edit() {
		Tag oldTag = loadTagById(id);

		apply(tag, oldTag);

		tag = oldTag;

		return TextResult.of("edit-" + tag);
	}

	@RestAction(value = "{id}")
	public TextResult save() {
		tag.setTagId(id);

		// save tag

		return TextResult.of("save-" + tag);
	}


	@GET @RestAction("boom")
	public TextResult exception() {
		int b = 0;
		int a = 5 / b;
		return TextResult.of("Hi" + a);
	}

}