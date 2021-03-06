package com.example.core;

import java.util.Random;
import java.util.List;
import java.util.BitSet;
import java.util.ArrayList;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Iterator;

public final class DefaultDecoder implements Decoder {

  private final Random uniformRNG;
  private final int nPackets;
  private final List<Packet> undecodedPackets;
  private final Packet[] decodedPackets;
  private int nReceivedPackets;
  private int nDecodedPackets;

  private static final class Packet {
    private final byte[] data;
    private final int[] neighbours;

    public static final int[] NO_NEIGHBOURS = new int[0];

    public Packet(byte[] data, int[] neighbours) {
      this.data = data;
      this.neighbours = neighbours;
    }

    public byte[] getData() {
      return this.data;
    }

    public int[] getNeighbours() {
      return this.neighbours;
    }

    public boolean hasNeighbour(int packetId)
    {
      for(int i = 0; i < this.neighbours.length; i++)
      {
        if(this.neighbours[i] == packetId)
        {
          return true;
        }
      }
      return false;
    }

  }

  public DefaultDecoder(long seed, int nPackets) {
    this.uniformRNG = new Random(seed);
    this.nPackets = nPackets;
    this.undecodedPackets = new ArrayList<Packet>();
    this.decodedPackets = new Packet[nPackets];
  }

  private boolean packetIsDecodable(Packet packet)
  {
    int d = packet.getNeighbours().length;
    for(int neighbourId: packet.getNeighbours())
    {
      if(this.decodedPackets[neighbourId] != null)
        d--;
    }
    return d <= 1;
  }

  private int undecodedNeighbourId(Packet packet)
  {
    for(int neighbourId: packet.getNeighbours())
    {
      if(this.decodedPackets[neighbourId] == null)
        return neighbourId;
    }
    return -1;
  }

  private Packet decodePacket(Packet packet) {
    BitSet set = BitSet.valueOf(packet.getData());
    for(int i = 0; i < packet.getNeighbours().length; i++)
    {
      Packet neighbour = decodedPackets[packet.getNeighbours()[i]];
      if(neighbour != null)
        set.xor(BitSet.valueOf(neighbour.getData()));
    }
    return new Packet(set.toByteArray(), Packet.NO_NEIGHBOURS);
  }

  private void decodingStep() {
    Iterator<Packet> iter = this.undecodedPackets.iterator();

    while (iter.hasNext()) {
      Packet packet = iter.next();
      if(packetIsDecodable(packet))
      {
        int undecodedNeighbourId = undecodedNeighbourId(packet);
        if(undecodedNeighbourId >= 0)
        {
          decodedPackets[undecodedNeighbourId] = decodePacket(packet);
          nDecodedPackets++;
        }
        else
        {
        }
        iter.remove();
      }
    }
  }

  @Override
  public void write(OutputStream stream) throws IOException {

    for(Packet packet: decodedPackets)
    {
      byte[] data = packet.getData();
      stream.write(data, 0, data.length);
    }
  }

  @Override
  public boolean receive(DecodedPacket packet)
  {
    byte[] data = packet.getData();
    int[] neighbours = packet.getNeighbors();
    if(nDecodedPackets >= nPackets)
    {
      return true;
    }

    if(neighbours.length > 1)
    {
       this.undecodedPackets.add(new Packet(data, neighbours));
    }
    else
    {

      if(decodedPackets[neighbours[0]] == null)
      {
        this.decodedPackets[neighbours[0]] = new Packet(data, neighbours);
        nDecodedPackets++;
      }
    }

    nReceivedPackets++;

    if(nReceivedPackets > nPackets) {
      /* we need a great deal more than k = this.nPackets,
       * but for anything > k, we give it a try.
       * Yes, this means that work is done for nothing
       * FIXME: use exact lower bounds here
       *
       */
      decodingStep();
    }
    return isDecodingFinished();
  }

  @Override
  public boolean isDecodingFinished() {
    return nDecodedPackets >= nPackets;
  }

}
