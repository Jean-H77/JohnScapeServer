package com.ruse.net.packet;

import com.ruse.net.packet.impl.BadPacketListener;
import com.ruse.net.packet.impl.BankModifiableX;
import com.ruse.net.packet.impl.ButtonClickPacketListener;
import com.ruse.net.packet.impl.ChangeAppearancePacketListener;
import com.ruse.net.packet.impl.ChangeRelationStatusPacketListener;
import com.ruse.net.packet.impl.ChatPacketListener;
import com.ruse.net.packet.impl.ClickTextMenuPacketListener;
import com.ruse.net.packet.impl.CloseInterfacePacketListener;
import com.ruse.net.packet.impl.CommandPacketListener;
import com.ruse.net.packet.impl.DialoguePacketListener;
import com.ruse.net.packet.impl.DropItemPacketListener;
import com.ruse.net.packet.impl.DuelAcceptancePacketListener;
import com.ruse.net.packet.impl.DungeoneeringPartyInvitatationPacketListener;
import com.ruse.net.packet.impl.EnterInputPacketListener;
import com.ruse.net.packet.impl.EquipPacketListener;
import com.ruse.net.packet.impl.ExamineItemPacketListener;
import com.ruse.net.packet.impl.ExamineNpcPacketListener;
import com.ruse.net.packet.impl.FinalizedMapRegionChangePacketListener;
import com.ruse.net.packet.impl.FollowPlayerPacketListener;
import com.ruse.net.packet.impl.HeightCheckPacketListener;
import com.ruse.net.packet.impl.IdleLogoutPacketListener;
import com.ruse.net.packet.impl.ItemActionPacketListener;
import com.ruse.net.packet.impl.ItemContainerActionPacketListener;
import com.ruse.net.packet.impl.MagicOnItemsPacketListener;
import com.ruse.net.packet.impl.MagicOnPlayerPacketListener;
import com.ruse.net.packet.impl.MovementPacketListener;
import com.ruse.net.packet.impl.NPCOptionPacketListener;
import com.ruse.net.packet.impl.ObjectActionPacketListener;
import com.ruse.net.packet.impl.PickupItemPacketListener;
import com.ruse.net.packet.impl.PlayerOptionPacketListener;
import com.ruse.net.packet.impl.PlayerRelationPacketListener;
import com.ruse.net.packet.impl.PrestigeSkillPacketListener;
import com.ruse.net.packet.impl.RegionChangePacketListener;
import com.ruse.net.packet.impl.SendClanChatMessagePacketListener;
import com.ruse.net.packet.impl.SilencedPacketListener;
import com.ruse.net.packet.impl.SwitchItemSlotPacketListener;
import com.ruse.net.packet.impl.TeleportPacketListener;
import com.ruse.net.packet.impl.TradeInvitationPacketListener;
import com.ruse.net.packet.impl.UseItemPacketListener;
import com.ruse.net.packet.impl.WithdrawMoneyFromPouchPacketListener;

public class PacketConstants {

	public static final PacketListener[] PACKETS = new PacketListener[257];

	static {
		for(int i = 0; i < PACKETS.length; i++)
			PACKETS[i] = new SilencedPacketListener();
		PACKETS[4] = PACKETS[230] = new ChatPacketListener();
		PACKETS[EquipPacketListener.OPCODE] = new EquipPacketListener();
		PACKETS[141] = new BankModifiableX();
		PACKETS[109] = new BadPacketListener();
		PACKETS[87] = new DropItemPacketListener();
		PACKETS[103] = new CommandPacketListener();	
		PACKETS[121] = new FinalizedMapRegionChangePacketListener();	
		PACKETS[130] = new CloseInterfacePacketListener();
		PACKETS[ButtonClickPacketListener.OPCODE] = new ButtonClickPacketListener();
		PACKETS[2] = new ExamineItemPacketListener();
		PACKETS[6] = new ExamineNpcPacketListener();
		PACKETS[5] = new SendClanChatMessagePacketListener();
		PACKETS[7] = new WithdrawMoneyFromPouchPacketListener();
		PACKETS[8] = new ChangeRelationStatusPacketListener();
		PACKETS[11] = new ChangeAppearancePacketListener();
		PACKETS[202] = new IdleLogoutPacketListener();
		PACKETS[131] = new NPCOptionPacketListener();
		PACKETS[17] = new NPCOptionPacketListener();
		PACKETS[18] = new NPCOptionPacketListener();
		PACKETS[21] = new NPCOptionPacketListener();
		PACKETS[210] = new RegionChangePacketListener();
		PACKETS[214] = new SwitchItemSlotPacketListener();
		PACKETS[236] = new PickupItemPacketListener();
		PACKETS[73] = new FollowPlayerPacketListener();
		PACKETS[NPCOptionPacketListener.ATTACK_NPC] = PACKETS[NPCOptionPacketListener.FIRST_CLICK_OPCODE] =
				new NPCOptionPacketListener();
		PACKETS[EnterInputPacketListener.ENTER_SYNTAX_OPCODE] =
				PACKETS[EnterInputPacketListener.ENTER_AMOUNT_OPCODE] = new EnterInputPacketListener();
		PACKETS[UseItemPacketListener.ITEM_ON_GROUND_ITEM] = PACKETS[UseItemPacketListener.ITEM_ON_ITEM] = 
				PACKETS[UseItemPacketListener.ITEM_ON_NPC] = PACKETS[UseItemPacketListener.ITEM_ON_OBJECT] = 
				PACKETS[UseItemPacketListener.ITEM_ON_PLAYER] = new UseItemPacketListener();
		PACKETS[UseItemPacketListener.USE_ITEM] = new UseItemPacketListener();
		PACKETS[TradeInvitationPacketListener.TRADE_OPCODE] = new TradeInvitationPacketListener();
		PACKETS[TradeInvitationPacketListener.CHATBOX_TRADE_OPCODE] = new TradeInvitationPacketListener();
		PACKETS[DialoguePacketListener.DIALOGUE_OPCODE] = new DialoguePacketListener();
		PACKETS[PlayerRelationPacketListener.ADD_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.REMOVE_FRIEND_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.ADD_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.REMOVE_IGNORE_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[PlayerRelationPacketListener.SEND_PM_OPCODE] = new PlayerRelationPacketListener();
		PACKETS[MovementPacketListener.COMMAND_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[MovementPacketListener.GAME_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[MovementPacketListener.MINIMAP_MOVEMENT_OPCODE] = new MovementPacketListener();
		PACKETS[TeleportPacketListener.PACKET_OPCODE] = new TeleportPacketListener();
		PACKETS[ObjectActionPacketListener.FIRST_CLICK] = PACKETS[ObjectActionPacketListener.SECOND_CLICK] =
				PACKETS[ObjectActionPacketListener.THIRD_CLICK] = PACKETS[ObjectActionPacketListener.FOURTH_CLICK] =
				PACKETS[ObjectActionPacketListener.FIFTH_CLICK] = new ObjectActionPacketListener();
		PACKETS[ItemContainerActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = 
				PACKETS[ItemContainerActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.FOURTH_ITEM_ACTION_OPCODE] =
				PACKETS[ItemContainerActionPacketListener.FIFTH_ITEM_ACTION_OPCODE] = PACKETS[ItemContainerActionPacketListener.SIXTH_ITEM_ACTION_OPCODE] = new ItemContainerActionPacketListener();
		PACKETS[ItemActionPacketListener.SECOND_ITEM_ACTION_OPCODE] = PACKETS[ItemActionPacketListener.THIRD_ITEM_ACTION_OPCODE] = PACKETS[ItemActionPacketListener.FIRST_ITEM_ACTION_OPCODE] = new ItemActionPacketListener();
		PACKETS[MagicOnItemsPacketListener.MAGIC_ON_ITEMS] = new MagicOnItemsPacketListener();
		PACKETS[MagicOnItemsPacketListener.MAGIC_ON_GROUNDITEMS] = new MagicOnItemsPacketListener();
		PACKETS[249] = new MagicOnPlayerPacketListener();
		PACKETS[153] = new PlayerOptionPacketListener();
		PACKETS[DuelAcceptancePacketListener.OPCODE] = new DuelAcceptancePacketListener();
		PACKETS[12] = new DungeoneeringPartyInvitatationPacketListener();
		PACKETS[222] = new ClickTextMenuPacketListener();
		PACKETS[223] = new PrestigeSkillPacketListener();
		PACKETS[229] = new HeightCheckPacketListener();
	}
	
	/**
	 * The size of packets sent from client to the server 
	 * used to decode them.
	 */
	public final static int[] MESSAGE_SIZES = {
			0, // 0
			0, // 1
			2, // 2
			1, // 3
			-1, // 4
			-1, // 5
			2, // 6
			4, // 7
			4, // 8
			4, // 9
			4, // 10
			-1, // 11
			-1, // 12
			-1, // 13
			8, // 14
			0, // 15
			6, // 16
			2, // 17
			2, // 18
			0, // 19
			0, // 20
			2, // 21
			0, // 22
			6, // 23
			0, // 24
			12, // 25
			0, // 26
			0, // 27
			0, // 28
			0, // 29
			9, // 30
			0, // 31
			0, // 32
			0, // 33
			0, // 34
			8, // 35
			4, // 36
			0, // 37
			0, // 38
			2, // 39
			2, // 40
			6, // 41
			0, // 42
			6, // 43
			0, // 44
			-1, // 45
			0, // 46
			0, // 47
			0, // 48
			1, // 49
			0, // 50
			0, // 51
			0, // 52
			12, // 53
			0, // 54
			0, // 55
			0, // 56
			8, // 57
			8, // 58
			0, // 59
			-1, // 60
			8, // 61
			0, // 62
			0, // 63
			0, // 64
			0, // 65
			0, // 66
			0, // 67
			0, // 68
			0, // 69
			8, // 70
			0, // 71
			2, // 72
			2, // 73
			8, // 74
			6, // 75
			0, // 76
			-1, // 77
			0, // 78
			6, // 79
			-1, // 80
			0, // 81
			0, // 82
			0, // 83
			0, // 84
			1, // 85
			4, // 86
			6, // 87
			0, // 88
			0, // 89
			0, // 90
			0, // 91
			0, // 92
			0, // 93
			0, // 94
			3, // 95
			0, // 96
			0, // 97
			-1, // 98
			0, // 99
			0, // 100
			13, // 101
			0, // 102
			-1, // 103
			0, // 104
			0, // 105
			0, // 106
			0, // 107
			0, // 108
			0, // 109
			0, // 110
			0, // 111
			0, // 112
			0, // 113
			0, // 114
			0, // 115
			0, // 116
			6, // 117
			0, // 118
			0, // 119
			1, // 120
			0, // 121
			6, // 122
			0, // 123
			0, // 124
			0, // 125
			-1, // 126
			0, // 127
			2, // 128
			6, // 129
			0, // 130
			4, // 131
			8, // 132
			8, // 133
			0, // 134
			6, // 135
			0, // 136
			0, // 137
			6, // 138
			2, // 139
			0, // 140
			10, // 141
			0, // 142
			0, // 143
			0, // 144
			6, // 145
			0, // 146
			0, // 147
			0, // 148
			6, // 149
			0, // 150
			0, // 151
			1, // 152
			2, // 153
			0, // 154
			2, // 155
			6, // 156
			0, // 157
			0, // 158
			0, // 159
			0, // 160
			0, // 161
			0, // 162
			0, // 163
			-1, // 164
			-1, // 165
			0, // 166
			0, // 167
			0, // 168
			0, // 169
			0, // 170
			0, // 171
			0, // 172
			0, // 173
			0, // 174
			0, // 175
			0, // 176
			0, // 177
			0, // 178
			0, // 179
			0, // 180
			8, // 181
			0, // 182
			3, // 183
			0, // 184
			2, // 185
			0, // 186
			0, // 187
			8, // 188
			1, // 189
			0, // 190
			0, // 191
			14, // 192
			0, // 193
			0, // 194
			0, // 195
			0, // 196
			0, // 197
			0, // 198
			0, // 199
			2, // 200
			0, // 201
			0, // 202
			0, // 203
			2, // 204
			0, // 205
			0, // 206
			0, // 207
			4, // 208
			0, // 209
			4, // 210
			0, // 211
			0, // 212
			0, // 213
			7, // 214
			8, // 215
			0, // 216
			0, // 217
			10, // 218
			0, // 219
			0, // 220
			0, // 221
			3, // 222
			2, // 223
			0, // 224
			0, // 225
			-1, // 226
			0, // 227
			8, // 228
			1, // 229
			1, // 230
			0, // 231
			0, // 232
			0, // 233
			8, // 234
			0, // 235
			6, // 236
			8, // 237
			1, // 238
			0, // 239
			0, // 240
			4, // 241
			2, // 242
			0, // 243
			0, // 244
			0, // 245
			-1, // 246
			0, // 247
			-1, // 248
			4, // 249
			0, // 250
			0, // 251
			8, // 252
			6, // 253
			0, // 254
			0, // 255

	};

//	public static void main(String...args) {
//		for (int index = 0; index < MESSAGE_SIZES.length; index++) {
//			System.out.println(MESSAGE_SIZES[index] + ", " + "// " + index);
//		}
//
//		/*int last = 0;
//		for (int index = 0; index < MESSAGE_SIZES.length; index++) {
//			if (index % 10 == 0 && index != 0) {
//				System.out.print("//" + last + " - " + (index - 1));
//				System.out.println();
//				last = index;
//			}
//			System.out.print(MESSAGE_SIZES[index] + ", ");
//			if (index == MESSAGE_SIZES.length - 1) {
//				System.out.print("//" + last + " - " + (index - 1));
//			}
//		}*/
//	}
}
