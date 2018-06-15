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

	private Option findOptionWithLongName(final String longName) {
		for (final Option option : options) {
			if (longName.equals(option.longName)) {
				return option;
			}
		}
		throw new CliException("Unknown option: " + longName);
	}
	private Option findOptionWithLongName(final String longName, final String valueToConsume) {
		for (final Option option : options) {
			if (longName.equals(option.longName)) {
				if (option.hasArg && valueToConsume == null) {
					throw new CliException("Option value not provided for: " + longName);
				}
				return option;
			}
		}
		throw new CliException("Unknown option: " + longName);
	}
	private Option findOptionWithShortName(final String shortName) {
		for (final Option option : options) {
			if (shortName.equals(option.shortName)) {
				return option;
			}
		}
		throw new CliException("Unknown option: " + shortName);
	}
	private Option findOptionWithShortName(final String shortName, final String valueToConsume) {
		for (final Option option : options) {
			if (shortName.equals(option.shortName)) {
				if (option.hasArg && valueToConsume == null) {
					throw new CliException("Option value not provided for: " + shortName);
				}
				return option;
			}
		}
		throw new CliException("Unknown option: " + shortName);
	}
	private Option findOptionWithShortNameAndNoArguments(final String shortName) {
		for (final Option option : options) {
			if (shortName.equals(option.shortName) && !option.hasArg) {
				return option;
			}
		}
		throw new CliException("Unknown option: " + shortName);
	}

	@Override
	public void accept(final String... args) {
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

					final Option option = findOptionWithLongName(argLongName, value);
					option.consumer.accept(option.hasArg ? value : argLongName);
					args[i] = null;
					continue;
				}

				// short names
				if (arg.startsWith("-")) {
					final String argShortName = arg.substring(1);

					if (argShortName.length() > 1) {
						// compressed options
						final char[] allShortNames = argShortName.toCharArray();
						for (final char c : allShortNames) {
							final String argName = String.valueOf(c);
							final Option option = findOptionWithShortNameAndNoArguments(argName);
							option.consumer.accept(argName);
						}
						args[i] = null;
						continue;
					}

					final Option option = findOptionWithShortName(argShortName, value);
					option.consumer.accept(option.hasArg ? value : argShortName);
					args[i] = null;
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

}
