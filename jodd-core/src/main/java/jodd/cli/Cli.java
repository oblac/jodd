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

package jodd.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Cli implements Consumer<String[]> {

	private final List<Option> options = new ArrayList<>();
	private final List<Param> params = new ArrayList<>();

	public Option option() {
		final Option option = new Option();
		options.add(option);
		return option;
	}

	public Param param() {
		final Param param = new Param();
		params.add(param);
		return param;
	}

	private boolean consumeOptionWithLongName(final String input, final String valueToConsume) {
		for (final Option option : options) {
			if (input.equals(option.longName)) {
				if (option.hasArg && valueToConsume == null) {
					throw new CliException("Option value not provided for: " + input);
				}
				option.consumer.accept(option.hasArg ? valueToConsume : input);
				return option.hasArg;
			}

			if (option.longName != null && input.startsWith(option.longName + "=")) {
				option.consumer.accept(input.substring(option.longName.length() + 1));
				return false;
			}
		}

		throw new CliException("Unknown option: " + input);
	}

	private boolean consumeOptionWithShortName(final String input, final String valueToConsume) {
		for (final Option option : options) {
			if (input.equals(option.shortName)) {
				if (option.hasArg) {
					if (valueToConsume == null) {
						throw new CliException("Option value not provided for: " + input);
					}
					option.consumer.accept(valueToConsume);
					return true;
				}
				else {
					option.consumer.accept(input);
					return false;
				}
			}

			if (option.shortName != null && input.startsWith(option.shortName + "=")) {
				option.consumer.accept(input.substring(option.shortName.length() + 1));
				return false;
			}
		}
		throw new CliException("Unknown option: " + input);
	}

	private void consumeOptionWithShortNameAndNoArguments(final String shortName) {
		for (final Option option : options) {
			if (shortName.equals(option.shortName) && !option.hasArg) {
				option.consumer.accept(shortName);
				return;
			}
		}
		throw new CliException("Unknown option: " + shortName);
	}

	@Override
	public void accept(final String... args) {
		assertConfigurationIsValid();

		boolean dontParseOptionsAnyMore = false;
		int i;
		int paramsIndex = 0;

		for (i = 0; i < args.length; i++) {
			final String arg = args[i];
			if (arg.isEmpty()) {
				continue;
			}
			final String value = (i + 1 < args.length) ? args[i + 1] : null;

			if (arg.equals("--")) {
				dontParseOptionsAnyMore = true;
				continue;
			}

			if (!dontParseOptionsAnyMore) {
				// long names
				if (arg.startsWith("--")) {
					final String argLongName = arg.substring(2);

					consumeOptionWithLongName(argLongName, value);

					args[i] = null;
					continue;
				}

				// short names
				if (arg.startsWith("-")) {
					final String argShortName = arg.substring(1);

					if (argShortName.length() > 1 && argShortName.charAt(1) != '=') {
						// compressed options
						final char[] allShortNames = argShortName.toCharArray();
						for (final char c : allShortNames) {
							final String argName = String.valueOf(c);
							consumeOptionWithShortNameAndNoArguments(argName);
						}
						args[i] = null;
						continue;
					}

					final boolean valueConsumed = consumeOptionWithShortName(argShortName, value);

					// mark argument as consumed
					args[i] = null;
					if (valueConsumed) {
						// mark value as consumed, too
						i++;
						args[i] = null;
					}
					continue;
				}
			}

			// params

			if (paramsIndex == params.size()) {
				// we are done here
				break;
			}
			final Param param = params.get(paramsIndex++);

			final List<String> paramArguments = new ArrayList<>();

			int from = 0;
			final int to = param.required + param.optional;

			for (; from < to; from++, i++) {
				final String paramValue = (i < args.length) ? args[i] : null;
				if (paramValue == null) {
					break;
				}
				paramArguments.add(paramValue);
			}
			i--;

			if (paramArguments.size() < param.required) {
				throw new CliException("Parameter required: " + param.label);
			}

			if (paramArguments.isEmpty()) {
				// parameter not found
				continue;
			}

			param.consumer.accept(paramArguments.toArray(new String[0]));
		}

		// must check if remaining parameters are not satisfied
		while (paramsIndex < params.size()) {
			final Param param = params.get(paramsIndex++);
			if (param.required > 0) {
				throw new CliException("Parameter required: " + param.label);
			}
		}
	}

	private void assertConfigurationIsValid() {
		for (final Option option : options) {
			if (option.consumer == null) {
				throw new CliException("Option has no registered consumer: " + option);
			}
		}
	}

	/**
	 * Prints the usage line.
	 */
	public void printUsage(final String commandName) {
		final StringBuilder usage = new StringBuilder(commandName);

		for (final Option option : options) {
			if (option.shortName != null) {
				usage.append(" [-").append(option.shortName).append("]");
			} else if (option.longName != null) {
				usage.append(" [--").append(option.longName).append("]");
			}
		}

		for (final Param param : params) {
			usage.append(" ").append(param.label);
		}

		System.out.println(usage);
	}

}
