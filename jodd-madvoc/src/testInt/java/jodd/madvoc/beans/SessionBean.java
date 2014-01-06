// Copyright (c) 2003-2014, Jodd Team (jodd.org). All Rights Reserved.

package jodd.madvoc.beans;

import jodd.petite.meta.PetiteBean;
import jodd.petite.scope.SessionScope;

import java.io.Serializable;

@PetiteBean(scope = SessionScope.class)
public class SessionBean implements Serializable {


}