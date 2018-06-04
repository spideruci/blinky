package org.spideruci.analysis.statik.controlflow;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

/**
 * The Bytenode is an encapsulation of a symbolic stack frame in a classfile,
 * while also storing the successors and predecessors of the stack frame, in the
 * context of a control flow graph.
 * @author vpalepu
 *
 * @param <V>
 */
class ByteNode<V extends Value> extends Frame {
  Set< ByteNode<V> > successors = new HashSet< ByteNode<V> >();
  Set< ByteNode<V> > predecessors = new HashSet< ByteNode<V> >();
  String label = "";

  public ByteNode(int nLocals, int nStack) {
    super(nLocals, nStack);
  }

  public ByteNode(Frame src) {
    super(src);
  }
}