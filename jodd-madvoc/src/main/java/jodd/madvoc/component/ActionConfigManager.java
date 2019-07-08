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

package jodd.madvoc.component;

import jodd.cache.TypeCache;
import jodd.madvoc.ActionConfig;
import jodd.madvoc.MadvocException;
import jodd.madvoc.meta.Action;
import jodd.madvoc.meta.ActionAnnotationValues;
import jodd.madvoc.meta.ActionConfiguredBy;
import jodd.petite.meta.PetiteInject;
import jodd.util.ArraysUtil;
import jodd.util.annotation.AnnotationParser;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.util.function.Consumer;

/**
 * Manager for action configurations and action annotation.
 */
public class ActionConfigManager {

	private final TypeCache<ActionConfig> actionConfigs = TypeCache.createDefault();
	private AnnotationParser[] annotationParsers = new AnnotationParser[0];

	@PetiteInject
	protected ContextInjectorComponent contextInjectorComponent;

	/**
	 * Registers action configuration for given annotation. New {@link ActionConfig} is created
	 * and stored.
	 */
	public void registerAnnotation(final Class<? extends Annotation> annotationType) {
		final ActionConfiguredBy actionConfiguredBy = annotationType.getAnnotation(ActionConfiguredBy.class);

		if (actionConfiguredBy == null) {
			throw new MadvocException("Action annotation is missing it's " + ActionConfiguredBy.class.getSimpleName() + " configuration.");
		}

		bindAnnotationConfig(annotationType, actionConfiguredBy.value());
	}

	/**
	 * Binds action annotation and the action config. This can overwrite the default annotation
	 * configuration of an annotation.
	 */
	public void bindAnnotationConfig(final Class<? extends Annotation> annotationType, final Class<? extends ActionConfig> actionConfigClass) {
		final ActionConfig actionConfig = registerNewActionConfiguration(actionConfigClass);

		actionConfigs.put(annotationType, actionConfig);

		for (final AnnotationParser annotationParser : annotationParsers) {
			if (annotationType.equals(annotationParser.getAnnotationType())) {
				// parser already exists
				return;
			}
		}
		annotationParsers = ArraysUtil.append(annotationParsers, new AnnotationParser(annotationType, Action.class));
	}


	/**
	 * Registers action configuration for given type.
	 */
	protected ActionConfig registerNewActionConfiguration(final Class<? extends ActionConfig> actionConfigClass) {
		final ActionConfig newActionConfig = createActionConfig(actionConfigClass);

		actionConfigs.put(actionConfigClass, newActionConfig);

		return newActionConfig;
	}

	/**
	 * Lookup for the action configuration. Typically, the input argument is either the action type or annotation type.
	 */
	public ActionConfig lookup(final Class actionTypeOrAnnotationType) {
		final ActionConfig actionConfig = actionConfigs.get(actionTypeOrAnnotationType);

		if (actionConfig == null) {
			throw new MadvocException("ActionConfiguration not registered:" + actionTypeOrAnnotationType.getName());
		}
		return actionConfig;
	}

	/**
	 * Fetch some action config and consumes it.
	 */
	public <T extends ActionConfig> void with(final Class<T> actionConfigType, final Consumer<T> actionConfigConsumer) {
		final T actionConfig = (T) lookup(actionConfigType);
		actionConfigConsumer.accept(actionConfig);
	}

	/**
	 * Returns {@code true} if annotated element as action annotation on it.
	 */
	public boolean hasActionAnnotationOn(final AnnotatedElement annotatedElement) {
		for (final AnnotationParser annotationParser : annotationParsers) {
			if (annotationParser.hasAnnotationOn(annotatedElement)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Reads annotation value from annotated element. If annotation does not exist, returns {@code null}.
	 */
	public ActionAnnotationValues readAnnotationValue(final AnnotatedElement annotatedElement) {
		for (final AnnotationParser annotationParser : annotationParsers) {
			if (annotationParser.hasAnnotationOn(annotatedElement)) {
				return ActionAnnotationValues.of(annotationParser, annotatedElement);
			}
		}
		return null;
	}

	protected ActionConfig createActionConfig(final Class<? extends ActionConfig> actionConfigClass) {
		try {
			final Constructor<? extends ActionConfig> ctor = actionConfigClass.getDeclaredConstructor();
			final ActionConfig actionConfig = ctor.newInstance();

			contextInjectorComponent.injectContext(actionConfig);

			return actionConfig;
		}
		catch (Exception ex) {
			throw new MadvocException("Invalid action configuration class: " + actionConfigClass.getSimpleName(), ex);
		}
	}
}