/*
 * IzPack - Copyright 2001-2013 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2012 Tim Anderson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.core.handler;

import com.izforge.izpack.api.handler.AbstractPrompt;
import com.izforge.izpack.api.handler.Prompt;
import com.izforge.izpack.api.resource.Messages;
import com.izforge.izpack.util.Console;

/**
 * Console implementation of {@link Prompt}.
 *
 * @author Tim Anderson
 */
public class ConsolePrompt extends AbstractPrompt
{
    /**
     * The console.
     */
    private final Console console;

    /**
     * OK-Cancel prompt.
     */
    private final String okCancelPrompt;

    /**
     * Yes-No prompt.
     */
    private final String yesNoPrompt;

    /**
     * Yes-No-Cancel prompt.
     */
    private final String yesNoCancelPrompt;

    /**
     * 'OK' response value.
     */
    private final String ok;

    /**
     * 'Cancel' response value.
     */
    private final String cancel;

    /**
     * 'Yes' response value.
     */
    private final String yes;

    /**
     * 'No' response value.
     */
    private final String no;


    /**
     * Constructs a {@code ConsolePrompt}.
     *
     * @param console  the console
     * @param messages the messages to localise the prompt
     */
    public ConsolePrompt(Console console, Messages messages)
    {
        this.console = console;
        okCancelPrompt = messages.get("ConsolePrompt.okCancel");
        yesNoPrompt = messages.get("ConsolePrompt.yesNo");
        yesNoCancelPrompt = messages.get("ConsolePrompt.yesNoCancel");
        ok = messages.get("ConsolePrompt.ok");
        cancel = messages.get("ConsolePrompt.cancel");
        yes = messages.get("ConsolePrompt.yes");
        no = messages.get("ConsolePrompt.no");
    }

    @Override
    public void message(Type type, String title, String message, Throwable throwable)
    {
        if (title != null)
        {
            console.println(title + ":");
        }
        console.println(message);
        if (throwable != null)
        {
            console.println(getDetails(throwable));
        }
    }

    private static String getDetails(Throwable throwable)
    {
        StringBuffer b = new StringBuffer();
        int lengthOfLastTrace = 1; // initial value
        // Start with the specified throwable and loop through the chain of
        // causality for the throwable.
        while (throwable != null)
        {
            // Output Exception name and message, and begin a list
            b.append(throwable.getClass().getName() + ": " + throwable.getMessage());
            // Get the stack trace and output each frame.
            // Be careful not to repeat stack frames that were already reported
            // for the exception that this one caused.
            StackTraceElement[] stack = throwable.getStackTrace();
            for (int i = stack.length - lengthOfLastTrace; i >= 0; i--)
            {
                b.append("- in " + stack[i].getClassName() + "." + stack[i].getMethodName()
                        + "() at " + stack[i].getFileName() + ":"
                        + stack[i].getLineNumber() + "\n");
            }
            // See if there is a cause for this exception
            throwable = throwable.getCause();
            if (throwable != null)
            {
                // If so, output a header
                b.append("Caused by: ");
                // And remember how many frames to skip in the stack trace
                // of the cause exception
                lengthOfLastTrace = stack.length;
            }
        }
        return b.toString();
    }

    /**
     * Displays a confirmation message.
     *
     * @param type          the type of the message
     * @param title         the message title. May be {@code null}
     * @param message       the message
     * @param options       the options which may be selected
     * @param defaultOption the default option to select. May be {@code null}
     * @return the selected option
     */
    @Override
    public Option confirm(Type type, String title, String message, Options options, Option defaultOption)
    {
        Option result;
        console.println(message);
        if (options == Options.OK_CANCEL)
        {
            String defaultValue = (defaultOption != null && defaultOption == Option.OK) ? ok : cancel;
            String selected = console.prompt(okCancelPrompt, new String[]{ok, cancel}, defaultValue);
            if (ok.equals(selected))
            {
                result = Option.OK;
            }
            else
            {
                result = Option.CANCEL;
            }
        }
        else if (options == Options.YES_NO_CANCEL)
        {
            String defaultValue = cancel;
            if (defaultOption != null)
            {
                if (defaultOption == Option.YES)
                {
                    defaultValue = yes;
                }
                else if (defaultOption == Option.NO)
                {
                    defaultValue = no;
                }
            }
            String selected = console.prompt(yesNoCancelPrompt, new String[]{yes, no, cancel}, defaultValue);
            if (yes.equals(selected))
            {
                result = Option.YES;
            }
            else if (no.equals(selected))
            {
                result = Option.NO;
            }
            else
            {
                result = Option.CANCEL;
            }
        }
        else
        {
            String defaultValue = no;
            if (defaultOption != null && defaultOption == Option.YES)
            {
                defaultValue = yes;
            }
            String selected = console.prompt(yesNoPrompt, new String[]{yes, no}, defaultValue);
            if (yes.equals(selected))
            {
                result = Option.YES;
            }
            else
            {
                result = Option.NO;
            }
        }
        return result;
    }
}
