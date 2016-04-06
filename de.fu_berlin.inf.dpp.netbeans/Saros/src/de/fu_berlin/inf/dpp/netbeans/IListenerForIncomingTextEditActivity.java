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
 * Modifies the target file in order to apply the incoming changes, the
 * filecontaining project and the files are created if they don't exits.
 */
public interface IListenerForIncomingTextEditActivity {

	/**
	 * the filecontaining project and the file are going to be created if they
	 * don't exist, the file gets modified according to the incoming changes
	 */
	void processIncomingDocumentModification(
			TextEditActivity incomingTextEditActivity);
}
