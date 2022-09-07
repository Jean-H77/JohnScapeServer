package com.ruse.world.content.trading_post.buying_page;

import com.ruse.model.entity.character.player.Player;
import com.ruse.world.content.trading_post.Listing;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Buyer {

    private final Listing listing;
    private final Player buyer;
    private final int amountToBuy;
}
