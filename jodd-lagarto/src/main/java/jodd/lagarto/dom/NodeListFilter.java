// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.lagarto.dom;

import java.util.List;

public interface NodeListFilter {

	boolean accept(List<Node> currentResults, Node node, int index);
}
