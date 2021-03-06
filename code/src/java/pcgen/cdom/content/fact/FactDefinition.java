/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.content.fact;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.content.ContentDefinition;
import pcgen.cdom.enumeration.FactKey;
import pcgen.output.actor.FactKeyActor;
import pcgen.output.wrapper.CDOMObjectWrapper;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * A FactDefinition is a definition of a legal entry for a FACT: in a file. This
 * contains both the legal location (e.g. SKILL) as well as the name of the fact
 * (e.g. Possibility).
 * 
 * A FactDefinition is created when a FACTDEF: line is encountered in the Data
 * Control LST file.
 * 
 * @param <T>
 *            The Type of object upon which the FACT for this FactDefintion can
 *            be applied
 */
public class FactDefinition<T extends CDOMObject, F> extends
		ContentDefinition<T, F> implements FactInfo<T, F>
{

	/**
	 * The Fact Name for this FactDefinition.
	 */
	private String factName;

	/**
	 * @see pcgen.cdom.content.ContentDefinition#activateOutput()
	 */
	@Override
	protected void activateOutput()
	{
		FactKeyActor<?> fca = new FactKeyActor<>(getFactKey());
		CDOMObjectWrapper cow = CDOMObjectWrapper.getInstance();
		if (!cow.load(getUsableLocation(), factName.toLowerCase(), fca))
		{
			Logging.errorPrint(getUsableLocation().getSimpleName() + " output "
				+ factName.toLowerCase()
				+ " already exists, ignoring Visibility to EXPORT for FACT: "
				+ factName);
		}
	}

	/**
	 * @see pcgen.cdom.content.ContentDefinition#activateTokens(pcgen.rules.context.LoadContext)
	 */
	@Override
	protected void activateTokens(LoadContext context)
	{
		context.loadLocalToken(new FactParser<T, F>(this));
		Boolean required = getRequired();
		if ((required != null) && required.booleanValue())
		{
			context.loadLocalToken(new FactDefinitionEnforcer<T, F>(this));
		}
		Boolean selectable = getSelectable();
		if ((selectable != null) && selectable.booleanValue())
		{
			context.loadLocalToken(new FactGroupDefinition<T, F>(this));
		}
	}

	/**
	 * Sets the Fact Name for this FactDefinition
	 * 
	 * @param name
	 *            The Fact Name for this FactDefinition
	 * @throws IllegalArgumentException
	 *             if the given name is null or empty
	 */
	public void setFactName(String name)
	{
		if (name == null)
		{
			throw new IllegalArgumentException("Fact Name cannot be null");
		}
		if (name.length() == 0)
		{
			throw new IllegalArgumentException("Fact Name cannot be empty");
		}
		factName = name;
	}

	/**
	 * @see pcgen.cdom.content.fact.FactInfo#getFactName()
	 */
	@Override
	public String getFactName()
	{
		return factName;
	}

	/**
	 * @see pcgen.cdom.content.fact.FactInfo#getFactKey()
	 */
	@Override
	public FactKey<F> getFactKey()
	{
		return FactKey.getConstant(getFactName(), getFormatManager());
	}

}
