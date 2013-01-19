// Copyright (c) 2003-2013, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.util.LinkedList;

public interface NodeListFilter {

	boolean accept(LinkedList<Node> currentResults, Node node, int index);
}
