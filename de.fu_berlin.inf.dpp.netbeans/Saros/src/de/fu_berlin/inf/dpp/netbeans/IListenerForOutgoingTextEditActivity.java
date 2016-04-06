/*
 * Copyright (C) 2015 Freie Universitaet Berlin - Fachbereich Mathematik und Informatik
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package de.fu_berlin.inf.dpp.netbeans;

import de.fu_berlin.inf.dpp.activities.TextEditActivity;

/**
 * The parameters of a given TextEditActivity for outgoing changes of the
 * modified file are passed to variables and are written into a textfile.
 */
public interface IListenerForOutgoingTextEditActivity {

	/**
	 * parameters of a TexteditActivity for a changed file are passed to
	 * variables and are written into a textfile
	 */
	void processOutgoingDocumentModification(
			TextEditActivity outgoingTextEditActivity);
}
