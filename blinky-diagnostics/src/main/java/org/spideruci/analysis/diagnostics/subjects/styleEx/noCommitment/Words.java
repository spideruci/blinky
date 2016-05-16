/*
@author Charles.Y.Feng
@date May 12, 2016 4:59:00 PM
*/

package org.spideruci.analysis.diagnostics.subjects.styleEx.noCommitment;

import java.io.IOException;
import java.util.ArrayList;

interface Words{
    public ArrayList<String> extractWords(String fileName) throws IOException;
}

