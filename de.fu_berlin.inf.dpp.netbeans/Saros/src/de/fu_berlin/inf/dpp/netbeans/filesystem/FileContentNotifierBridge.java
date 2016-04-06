/*
 * Copyright (C) 2016 privateuser
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
package de.fu_berlin.inf.dpp.netbeans.filesystem;

import de.fu_berlin.inf.dpp.filesystem.IFile;
import de.fu_berlin.inf.dpp.filesystem.IFileContentChangedListener;
import de.fu_berlin.inf.dpp.filesystem.IFileContentChangedNotifier;
import de.fu_berlin.inf.dpp.filesystem.IResource;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Bridge class that maps Eclipse Resource change events to unique identifiers
 * by retrieving the absolute path relative to the workspace and converting the
 * path to a unique string.
 * 
 * @author Stefan Rossbach
 */
public class FileContentNotifierBridge implements IFileContentChangedNotifier{

    private CopyOnWriteArrayList<IFileContentChangedListener> fileContentChangedListeners = new CopyOnWriteArrayList<IFileContentChangedListener>();

    public FileContentNotifierBridge() {
        
    }

  

    @Override
    public void addFileContentChangedListener(
        IFileContentChangedListener listener) {
        fileContentChangedListeners.add(listener);

    }

    @Override
    public void removeFileContentChangedListener(
        IFileContentChangedListener listener) {
        fileContentChangedListeners.remove(listener);
    }
}

