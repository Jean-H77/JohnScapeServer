package com.ruse.model.definitions;

import com.ruse.world.clip.stream.ByteStream;
import com.ruse.world.clip.stream.ByteStreamExt;


// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class GameObjectDefinition {

	public enum DataType {
		STANDARD, OSRS
	}

	public static void init() {
		dataBuffer = new ByteStreamExt[3];
		streamIndices = new int[3][];

		int index = 0;
		dataBuffer[index] = new ByteStreamExt(getBuffer("loc.dat"));
		ByteStreamExt idxBuffer = new ByteStreamExt(getBuffer("loc.idx"));
		int totalObjects = idxBuffer.readUnsignedShort();
		streamIndices[index] = new int[totalObjects];
		int i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[index][j] = i;
			i += idxBuffer.readUnsignedShort();
		}

		index = 1;
		dataBuffer[index] = new ByteStreamExt(getBuffer("667loc.dat"));
		idxBuffer = new ByteStreamExt(getBuffer("667loc.idx"));
		totalObjects = idxBuffer.readUnsignedShort();
		streamIndices[index] = new int[totalObjects];
		i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[index][j] = i;
			i += idxBuffer.readUnsignedShort();
		}

		index = 2;
		dataBuffer[index] = new ByteStreamExt(getBuffer("loc_osrs.dat"));
		idxBuffer = new ByteStreamExt(getBuffer("loc_osrs.idx"));
		totalObjects = idxBuffer.readUnsignedShort();
		streamIndices[index] = new int[totalObjects];
		i = 2;
		for (int j = 0; j < totalObjects; j++) {
			streamIndices[index][j] = i;
			i += idxBuffer.readUnsignedShort();
		}

		cache = new GameObjectDefinition[20];
		for (int k = 0; k < 20; k++) {
			cache[k] = new GameObjectDefinition();
		}
	}

	public static GameObjectDefinition forId(int id) {
		if (id == -1)
			return null;

		for(int j = 0; j < 20; j++) {
			if(cache[j].id == id) {
				return cache[j];
			}
		}

		cacheIndex = (cacheIndex + 1) % 20;
		GameObjectDefinition object = cache[cacheIndex];

		int index;
		if (id >= 70_000) {
			object.dataType = DataType.OSRS;
			index = 2;
			id -= 70_000;
		} else if (id >= streamIndices[0].length) {
			object.dataType = DataType.STANDARD;
			index = 1;

			if(remove667(id)) {
				object.unwalkable = false;
				return object;
			}
		} else {
			object.dataType = DataType.STANDARD;
			index = 0;
			if(remove525(id)) {
				object.unwalkable = false;
				return object;
			}
		}

		if (id > streamIndices[index].length) {
			id = 0;
		}

		dataBuffer[index].currentOffset = streamIndices[index][id];
		object.id = id + (object.dataType == DataType.OSRS ? 70_000 : 0);
		object.nullLoader();
		try {
			if (index == 0) {
				object.readValues(dataBuffer[index]);
			} else if (index == 1) {
				object.readValues667(dataBuffer[index]);
			} else {
				object.readValuesOsrs(dataBuffer[index]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	public static byte[] getBuffer(String s) {
		try {
			java.io.File f = new java.io.File("./data/clipping/objects/" + s);
			if (!f.exists())
				return null;
			byte[] buffer = new byte[(int) f.length()];
			java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
			dis.readFully(buffer);
			dis.close();
			return buffer;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static boolean remove525(int i) {
		return i == 7332 || i == 7326 || i == 7325 || i == 7385
				|| i == 7331 || i == 7385 || i == 7320 || i == 7317 || i == 7323
				|| i == 7354 || i == 1536 || i == 1537 || i == 5126 || i == 1551
				|| i == 1553 || i == 1516 || i == 1519 || i == 1557 || i == 1558
				|| i == 7126 || i == 733 || i == 14233 || i == 14235 || i == 1596
				|| i == 1597 || i == 14751 || i == 14752 || i == 14923 || i == 36844
				|| i == 30864 || i == 2514 || i == 1805 || i == 15536 || i == 2399
				|| i == 14749 || i == 29315 || i == 29316 || i == 29319 || i == 29320
				|| i == 29360 || i == 1528 || i == 36913 || i == 36915 || i == 15516
				||i == 35549 || i == 35551 || i == 26808 || i == 26910 || i == 26913
				|| i == 24381 || i == 15514 || i == 25891 || i == 26082 || i == 26081
				|| i == 1530 || i == 16776 || i == 16778 || i == 28589 || i == 1533
				|| i == 17089 || i == 1600 || i == 1601 || i == 11707 || i == 24376
				|| i == 24378 || i == 40108 || i == 59 || i == 2069 || i == 36846
				|| i == 1508 || i == 1506 || i == 4031;
	}

	public static boolean remove667(int id) {
		return id == 11328 || id == 2956 || id == 463 || id == 462 || id == 25026
				|| id == 25020 || id == 25019 || id == 25024 || id == 25025 || id == 25016
				|| id == 10527 || id == 10529 || id == 40257 || id == 296 || id == 300 || id == 1747
				|| id == 7332 || id == 7326 || id == 7325 || id == 7385 || id == 7331
				|| id == 7320 || id == 7317 || id == 7323 || id == 7354 || id == 1536 || id == 1537
				|| id == 5126 || id == 1551 || id == 1553 || id == 1516 || id == 1519 || id == 1557
				|| id == 1558 || id == 7126 || id == 733 || id == 14233 || id == 14235 || id == 1596
				|| id == 1597 || id == 14751 || id == 14752 || id == 14923 || id == 36844 || id == 30864
				|| id == 2514 || id == 1805 || id == 15536 || id == 2399 || id == 14749 || id == 29315
				|| id == 29316 || id == 29319 || id == 29320 || id == 29360 || id == 1528 || id == 36913
				|| id == 36915 || id == 15516 ||id == 35549 || id == 35551 || id == 26808 || id == 26910
				|| id == 26913 || id == 24381 || id == 15514 || id == 25891 || id == 26082 || id == 26081
				|| id == 1530 || id == 16776 || id == 16778 || id == 28589 || id == 1533 || id == 17089
				|| id == 1600 || id == 1601 || id == 11707 || id == 24376 || id == 24378 || id == 40108
				|| id == 59 || id == 2069 || id == 36846 || id == 1508 || id == 1506 || id == 4031;
	}

	public void nullLoader() {
		modelArray = null;
		objectModelType = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		tileSizeX = 1;
		tileSizeY = 1;
		unwalkable = true;
		impenetrable = true;
		interactive = false;
		aBoolean762 = false;
		aBoolean769 = false;
		aBoolean764 = false;
		anInt781 = -1;
		anInt775 = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		aBoolean779 = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		anInt774 = -1;
		anInt749 = -1;
		childrenIDs = null;
	}

	private void readValues(ByteStreamExt buffer) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = buffer.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int k = buffer.readUnsignedByte();
					if (k > 0)
						if (modelArray == null || lowMem) {
							objectModelType = new int[k];
							modelArray = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								modelArray[k1] = buffer.readUnsignedShort();
								objectModelType[k1] = buffer.readUnsignedByte();
							}
						} else {
							buffer.currentOffset += k * 3;
						}
				} else if (opcode == 2)
					name = buffer.readString();
				else if (opcode == 3)
					description = buffer.readBytes();
				else if (opcode == 5) {
					int l = buffer.readUnsignedByte();
					if (l > 0)
						if (modelArray == null || lowMem) {
							objectModelType = null;
							modelArray = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								modelArray[l1] = buffer.readUnsignedShort();
						} else {
							;// buffer.currentOffset += l * 2;
						}
				} else if (opcode == 14)
					tileSizeX = buffer.readUnsignedByte();
				else if (opcode == 15)
					tileSizeY = buffer.readUnsignedByte();
				else if (opcode == 17)
					unwalkable = false;
				else if (opcode == 18)
					impenetrable = false;
				else if (opcode == 19) {
					i = buffer.readUnsignedByte();
					if (i == 1)
						interactive = true;
				} else if (opcode == 21)
					aBoolean762 = true;
				else if (opcode == 22)
					aBoolean769 = false;//
				else if (opcode == 23)
					aBoolean764 = true;
				else if (opcode == 24) {
					anInt781 = buffer.readUnsignedShort();
					if (anInt781 == 65535)
						anInt781 = -1;
				} else if (opcode == 28)
					anInt775 = buffer.readUnsignedByte();
				else if (opcode == 29)
					aByte737 = buffer.readSignedByte();
				else if (opcode == 39)
					aByte742 = buffer.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (actions == null)
						actions = new String[10];
					actions[opcode - 30] = buffer.readString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden"))
						actions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = buffer.readUnsignedByte();
					modifiedModelColors = new int[i1];
					originalModelColors = new int[i1];
					for (int i2 = 0; i2 < i1; i2++) {
						modifiedModelColors[i2] = buffer.readUnsignedShort();
						originalModelColors[i2] = buffer.readUnsignedShort();
					}
				} else if (opcode == 60)
					anInt746 = buffer.readUnsignedShort();
				else if (opcode == 62)
					aBoolean751 = true;
				else if (opcode == 64)
					aBoolean779 = false;
				else if (opcode == 65)
					anInt748 = buffer.readUnsignedShort();
				else if (opcode == 66)
					anInt772 = buffer.readUnsignedShort();
				else if (opcode == 67)
					anInt740 = buffer.readUnsignedShort();
				else if (opcode == 68)
					anInt758 = buffer.readUnsignedShort();
				else if (opcode == 69)
					anInt768 = buffer.readUnsignedByte();
				else if (opcode == 70)
					anInt738 = buffer.readSignedShort();
				else if (opcode == 71)
					anInt745 = buffer.readSignedShort();
				else if (opcode == 72)
					anInt783 = buffer.readSignedShort();
				else if (opcode == 73)
					aBoolean736 = true;
				else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75)
						continue;
					anInt760 = buffer.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
			anInt774 = buffer.readUnsignedShort();
			if (anInt774 == 65535)
				anInt774 = -1;
			anInt749 = buffer.readUnsignedShort();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = buffer.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = buffer.readUnsignedShort();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			interactive = modelArray != null
					&& (objectModelType == null || objectModelType[0] == 10);
			if (actions != null)
				interactive = true;
		}
		if (aBoolean766) {
			unwalkable = false;
			impenetrable = false;
		}
		if (anInt760 == -1)
			anInt760 = unwalkable ? 1 : 0;
	}
	
	private void readValues667(ByteStreamExt buffer) {
		int i = -1;
		label0: do {
			int opcode;
			do {
				opcode = buffer.readUnsignedByte();
				if (opcode == 0)
					break label0;
				if (opcode == 1) {
					int k = buffer.readUnsignedByte();
					if (k > 0)
						if (modelArray == null || lowMem) {
							objectModelType = new int[k];
							modelArray = new int[k];
							for (int k1 = 0; k1 < k; k1++) {
								modelArray[k1] = buffer.readUnsignedShort();
								objectModelType[k1] = buffer.readUnsignedByte();
							}
						} else {
							buffer.currentOffset += k * 3;
						}
				} else if (opcode == 2)
					name = buffer.readString();
				else if (opcode == 3)
					description = buffer.readBytes();
				else if (opcode == 5) {
					int l = buffer.readUnsignedByte();
					if (l > 0)
						if (modelArray == null || lowMem) {
							objectModelType = null;
							modelArray = new int[l];
							for (int l1 = 0; l1 < l; l1++)
								modelArray[l1] = buffer.readUnsignedShort();
						} else {
							;//buffer.offset += l * 2;
						}
				} else if (opcode == 14)
					tileSizeX = buffer.readUnsignedByte();
				else if (opcode == 15)
					tileSizeY = buffer.readUnsignedByte();
				else if (opcode == 17)
					unwalkable = false;
				else if (opcode == 18)
					impenetrable = false;
				else if (opcode == 19) {
					i = buffer.readUnsignedByte();
					if (i == 1)
						interactive = true;
				} else if (opcode == 21) {
					//conformable = true;
				} else if (opcode == 22) {
					//blackModel = false;//
				} else if (opcode == 23)
					aBoolean764 = true;
				else if (opcode == 24) {
					buffer.readUnsignedShort();
				} else if (opcode == 28)
					buffer.readUnsignedByte();
				else if (opcode == 29)
					aByte737 = buffer.readSignedByte();
				else if (opcode == 39)
					aByte742 = buffer.readSignedByte();
				else if (opcode >= 30 && opcode < 39) {
					if (actions == null)
						actions = new String[10];
					actions[opcode - 30] = buffer.readString();
					if (actions[opcode - 30].equalsIgnoreCase("hidden")
							|| actions[opcode - 30].equalsIgnoreCase("null"))
						actions[opcode - 30] = null;
				} else if (opcode == 40) {
					int i1 = buffer.readUnsignedByte();
					for (int i2 = 0; i2 < i1; i2++) {
						buffer.readUnsignedShort();
						buffer.readUnsignedShort();
					}
				} else if (opcode == 60)
					buffer.readUnsignedShort();
				else if (opcode == 62)
					aBoolean751 = true;
				else if (opcode == 64)
					aBoolean779 = false;
				else if (opcode == 65)
					anInt748 = buffer.readUnsignedShort();
				else if (opcode == 66)
					anInt772 = buffer.readUnsignedShort();
				else if (opcode == 67)
					anInt740 = buffer.readUnsignedShort();
				else if (opcode == 68)
					buffer.readUnsignedShort();
				else if (opcode == 69)
					anInt768 = buffer.readUnsignedByte();
				else if (opcode == 70)
					anInt738 = buffer.readSignedShort();
				else if (opcode == 71)
					anInt745 = buffer.readSignedShort();
				else if (opcode == 72)
					anInt783 = buffer.readSignedShort();
				else if (opcode == 73)
					aBoolean736 = true;
				else if (opcode == 74) {
					aBoolean766 = true;
				} else {
					if (opcode != 75)
						continue;
					anInt760 = buffer.readUnsignedByte();
				}
				continue label0;
			} while (opcode != 77);
				anInt774 = buffer.readUnsignedShort();
			if (anInt774 == 65535)
				anInt774 = -1;
				anInt749 = buffer.readUnsignedShort();
			if (anInt749 == 65535)
				anInt749 = -1;
			int j1 = buffer.readUnsignedByte();
			childrenIDs = new int[j1 + 1];
			for (int j2 = 0; j2 <= j1; j2++) {
				childrenIDs[j2] = buffer.readUnsignedShort();
				if (childrenIDs[j2] == 65535)
					childrenIDs[j2] = -1;
			}

		} while (true);
		if (i == -1) {
			interactive = modelArray != null && (objectModelType == null || objectModelType[0] == 10);
			if (actions != null)
				interactive = true;
		}
		if (aBoolean766) {
			unwalkable = false;
			impenetrable = false;
		}
		if (anInt760 == -1)
			anInt760 = unwalkable ? 1 : 0;
    }

	void readValuesOsrs(ByteStreamExt stream) {
		int i = -1;
		label0: do {
			int opcode = stream.readUnsignedByte();
			if (opcode == 0)
				break label0;
			if (opcode == 1) {
				int k = stream.readUnsignedByte();
				if (k > 0)
					stream.currentOffset += k * 3;

			} else if (opcode == 2)
				name = stream.readString();
			else if (opcode == 3)
				description = stream.readBytes();
			else if (opcode == 5) {
				int l = stream.readUnsignedByte();
				if (l > 0)
					stream.currentOffset += l * 2;

			} else if (opcode == 14)
				tileSizeX = stream.readUnsignedByte();
			else if (opcode == 15)
				tileSizeY = stream.readUnsignedByte();
			else if (opcode == 17)
				unwalkable = false;
			else if (opcode == 18)
				impenetrable = false;
			else if (opcode == 19) {
				i = stream.readUnsignedByte();
				if (i == 1)
					interactive = true;
			} else if (opcode == 21) {
				//adjustToTerrain = true;
			} else if (opcode == 22) {
				//nonFlatShading = false;
			} else if (opcode == 23)
				aBoolean764 = true;
			else if (opcode == 24) {
				stream.readUnsignedShort();
			} else if (opcode == 27) {
				//interact type
			} else if (opcode == 28)
				anInt775 = stream.readUnsignedByte();
			else if (opcode == 29)
				stream.readSignedByte();
			else if (opcode == 39)
				stream.readSignedByte();
			else if (opcode >= 30 && opcode < 39) {
				if (actions == null)
					actions = new String[10];
				actions[opcode - 30] = stream.readString();
				if (actions[opcode - 30].equalsIgnoreCase("hidden"))
					actions[opcode - 30] = null;
			} else if (opcode == 40) {
				int i1 = stream.readUnsignedByte();
				modifiedModelColors = new int[i1];
				originalModelColors = new int[i1];
				for (int i2 = 0; i2 < i1; i2++) {
					modifiedModelColors[i2] = stream.readUnsignedShort();
					originalModelColors[i2] = stream.readUnsignedShort();
				}
			} else if (opcode == 41) {
				int length = stream.readUnsignedByte();
				//System.out.println("opcode 41");
				for (int index = 0; index < length; ++index)
				{
					stream.readUnsignedShort();
					stream.readUnsignedShort();
				}
			} else if (opcode == 61)
				stream.readUnsignedShort();
			else if (opcode == 62)
				aBoolean751 = true;
			else if (opcode == 64)
				aBoolean779 = false;
			else if (opcode == 65)
				stream.readUnsignedShort();
			else if (opcode == 66)
				stream.readUnsignedShort();
			else if (opcode == 67)
				stream.readUnsignedShort();
			else if (opcode == 68)
				stream.readUnsignedShort();
			else if (opcode == 69)
				stream.readUnsignedByte();
			else if (opcode == 70)
				stream.readSignedShort();
			else if (opcode == 71)
				stream.readSignedShort();
			else if (opcode == 72)
				stream.readSignedShort();
			else if (opcode == 73)
				aBoolean736 = true;
			else if (opcode == 74) {
				aBoolean766 = true;
			} else if (opcode == 75) {
				anInt760 = stream.readUnsignedByte();
			} else if (opcode == 77) {
				anInt774 = stream.readUnsignedShort();
				if (anInt774 == 65535)
					anInt774 = -1;
				anInt749 = stream.readUnsignedShort();
				if (anInt749 == 65535)
					anInt749 = -1;
				int j1 = stream.readUnsignedByte();
				childrenIDs = new int[j1 + 1];
				for (int j2 = 0; j2 <= j1; j2++) {
					childrenIDs[j2] = stream.readUnsignedShort();
					if (childrenIDs[j2] == 65535)
						childrenIDs[j2] = -1;
				}
			} else if (opcode == 78)
			{
				stream.readUnsignedShort();
				stream.readUnsignedByte();
			}
			else if (opcode == 79)
			{
				stream.readUnsignedShort();
				stream.readUnsignedShort();
				stream.readUnsignedByte();
				int length = stream.readUnsignedByte();
				for (int index = 0; index < length; ++index)
				{
					stream.readUnsignedShort();
				}
			}
			else if (opcode == 81)
			{
				stream.readUnsignedByte();
			}
			else if (opcode == 82)
			{
				stream.readUnsignedShort();
			}
			else if (opcode == 92)
			{
				stream.readUnsignedShort();
				stream.readUnsignedShort();
				stream.readUnsignedShort();
				int length = stream.readUnsignedByte();
				for (int index = 0; index <= length; ++index)
				{
					stream.readUnsignedShort();
				}
			}
			else if (opcode == 249)
			{
				int length = stream.readUnsignedByte();
				for (int k = 0; k < length; k++)
				{
					boolean isString = stream.readUnsignedByte() == 1;
					stream.get24BitInt();
					if (isString)
					{
						stream.readString();
					}
					else
					{
						stream.readDWord();
					}
				}
			} else {
				System.err.println("Unrecognized object opcode: " + opcode);
			}
		} while (true);
		if (i == -1) {
			interactive = modelArray != null && (objectModelType == null || objectModelType[0] == 10);
			if (actions != null)
				interactive = true;
		}
		if (aBoolean766) {
			unwalkable = false;
			impenetrable = false;
		}
		if (anInt760 == -1)
			anInt760 = unwalkable ? 1 : 0;
	}

	public GameObjectDefinition() {
		id = -1;
	}

	public boolean hasActions() {
		return interactive;
	}

	public boolean hasName() {
		return name != null && name.length() > 1;
	}

	public int xLength() {
		return tileSizeX;
	}

	public int yLength() {
		return tileSizeY;
	}

	public static int[][] streamIndices;
	private static ByteStreamExt[] dataBuffer;


	public DataType dataType;
	public boolean aBoolean736;
	public byte aByte737;
	public int anInt738;
	public String name;
	public int anInt740;
	public byte aByte742;
	public int tileSizeX;
	public int anInt745;
	public int anInt746;
	public int[] originalModelColors;
	public int anInt748;
	public int anInt749;
	public boolean aBoolean751;
	public static boolean lowMem;
	public int id;
	public boolean impenetrable;
	public int anInt758;
	public int childrenIDs[];
	public int anInt760;
	public int tileSizeY;
	public boolean aBoolean762;
	public boolean aBoolean764;
	public boolean aBoolean766;
	public boolean unwalkable;
	public int anInt768;
	public boolean aBoolean769;
	public static int cacheIndex;
	public int anInt772;
	public int[] modelArray;
	public int anInt774;
	public int anInt775;
	public int[] objectModelType;
	public byte description[];
	public boolean interactive;
	public boolean aBoolean779;
	public int anInt781;
	public static GameObjectDefinition[] cache;
	public int anInt783;
	public int[] modifiedModelColors;
	public String actions[];

	public int actionCount() {
		return interactive ? 1 : 0;
	}

	public String getName() {
		return name;
	}
	
	public int getSizeX() {
		return tileSizeX;
	}
	
	public int getSizeY() {
		return tileSizeY;
	}

}
