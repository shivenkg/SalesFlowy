package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SeedData
import com.example.data.model.BadgeItem
import com.example.data.model.LeaderboardUser
import com.example.data.model.MilestoneProgress
import com.example.data.model.RewardItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.util.IndiaLocaleUtil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GamificationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val rewardCatalog by viewModel.rewardCatalog.collectAsState()
    val milestones by viewModel.milestones.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Leaderboard, 1: Badges & Milestones, 2: Reward Store
    var selectedTier by remember { mutableStateOf("Frontliners") }
    var selectedTimeframe by remember { mutableStateOf("Monthly") }

    val filteredLeaderboard = remember(selectedTier) {
        SeedData.leaderboards.filter { it.tier == selectedTier }
    }

    Scaffold(
        modifier = modifier.testTag("gamification_screen")
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Hero Gamification Points Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldMilestone.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(18.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(GoldMilestone.copy(alpha = 0.5f))
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Sales Arena & Rewards",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "14 Days Streak 🔥 • Rank #1 in North Zone",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = GoldMilestone,
                        modifier = Modifier.testTag("user_xp_wallet_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUser?.points ?: 3250} XP",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Tab Navigation Row
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Leaderboards", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Badges & Goals", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Rewards Store", fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // TAB 0: LEADERBOARD
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Filters Row
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Frontliners", "Hub Units", "Regions").forEach { tier ->
                                        FilterChip(
                                            selected = selectedTier == tier,
                                            onClick = { selectedTier = tier },
                                            label = { Text(tier, fontSize = 11.sp) }
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf("Monthly", "Weekly", "All-Time").forEach { tf ->
                                        SuggestionChip(
                                            onClick = { selectedTimeframe = tf },
                                            label = { Text(tf, fontSize = 10.sp, fontWeight = if (selectedTimeframe == tf) FontWeight.Bold else FontWeight.Normal) }
                                        )
                                    }
                                }
                            }
                        }

                        // Top Performer Podium Card for #1
                        val topPerformer = filteredLeaderboard.firstOrNull()
                        if (topPerformer != null) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = GoldMilestone.copy(alpha = 0.12f)),
                                    shape = RoundedCornerShape(16.dp),
                                    border = CardDefaults.outlinedCardBorder().copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(GoldMilestone)
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(CircleShape)
                                                .background(GoldMilestone),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.EmojiEvents, contentDescription = "Crown", tint = Color.Black, modifier = Modifier.size(26.dp))
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = topPerformer.name, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleSmall)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(shape = RoundedCornerShape(4.dp), color = GoldMilestone) {
                                                    Text("CHAMPION", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                }
                                            }
                                            Text(
                                                text = "${topPerformer.unitName} • ${topPerformer.badge}",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = IndiaLocaleUtil.formatInrShort(topPerformer.achievedAmount), fontWeight = FontWeight.ExtraBold, color = SuccessGreen, fontSize = 14.sp)
                                            Text(text = "${topPerformer.points} XP", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldMilestone)
                                        }
                                    }
                                }
                            }
                        }

                        // Full Leaderboard list
                        items(filteredLeaderboard) { user ->
                            LeaderboardRowCard(user = user)
                        }
                    }
                }
                1 -> {
                    // TAB 1: BADGES & MILESTONES
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = "Active Milestones & Progress",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(milestones) { milestone ->
                            MilestoneProgressCard(milestone = milestone)
                        }

                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Achievement Badges",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        items(SeedData.badges) { badge ->
                            BadgeProgressRowCard(badge = badge)
                        }
                    }
                }
                2 -> {
                    // TAB 2: REWARD STORE
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp, 12.dp, 16.dp, 80.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Redeemable Achievement Perks",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Trade hard-earned XP for real executive perks & bonuses",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        items(rewardCatalog) { reward ->
                            RewardCatalogItemCard(
                                reward = reward,
                                userPoints = currentUser?.points ?: 0,
                                onClaim = { viewModel.claimReward(reward.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneProgressCard(milestone: MilestoneProgress) {
    val progress = (milestone.currentCount.toFloat() / milestone.targetCount).coerceIn(0f, 1f)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = milestone.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${milestone.currentCount} / ${milestone.targetCount} ${milestone.unit}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BrandPrimaryLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = SuccessGreen,
                trackColor = SuccessGreen.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unlocks: ${milestone.badgeUnlocked}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "+${milestone.rewardPoints} XP Bonus",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldMilestone
                )
            }
        }
    }
}

@Composable
fun BadgeProgressRowCard(badge: BadgeItem) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (badge.unlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (badge.unlocked) GoldMilestone.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (badge.unlocked) GoldMilestone.copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (badge.unlocked) Icons.Default.MilitaryTech else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (badge.unlocked) GoldMilestone else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = badge.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (badge.unlocked) GoldMilestone.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = badge.level,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (badge.unlocked) GoldMilestone else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                    Text(
                        text = badge.description,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "+${badge.xpBonus} XP",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = GoldMilestone
                )
            }

            if (!badge.unlocked) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (badge.currentProgress.toFloat() / badge.targetProgress).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = WarningAmber,
                    trackColor = WarningAmber.copy(alpha = 0.2f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Progress: ${badge.currentProgress}/${badge.targetProgress} completed",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun RewardCatalogItemCard(
    reward: RewardItem,
    userPoints: Int,
    onClaim: () -> Unit
) {
    val canAfford = userPoints >= reward.pointsCost && !reward.isClaimed

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(
                if (reward.isClaimed) SuccessGreen.copy(alpha = 0.4f)
                else if (canAfford) GoldMilestone.copy(alpha = 0.5f)
                else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reward_item_${reward.id.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = reward.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = reward.category, style = MaterialTheme.typography.bodySmall, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (reward.isClaimed) SuccessGreen.copy(alpha = 0.15f) else GoldMilestone.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (reward.isClaimed) "REDEEMED" else "${reward.pointsCost} XP",
                        fontWeight = FontWeight.ExtraBold,
                        color = if (reward.isClaimed) SuccessGreen else Color(0xFFB45309),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (reward.isClaimed) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SuccessGreen.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Claimed on ${reward.claimedDate ?: "Today"}. Corporate voucher dispatched to email.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            } else {
                Button(
                    onClick = onClaim,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldMilestone,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("claim_reward_button_${reward.id.lowercase()}")
                ) {
                    Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (canAfford) "Redeem Voucher (${reward.pointsCost} XP)" else "Need ${reward.pointsCost - userPoints} More XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardRowCard(user: LeaderboardUser) {
    val rankColor = when (user.rank) {
        1 -> GoldMilestone
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_rank_${user.rank}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(rankColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#${user.rank}",
                    fontWeight = FontWeight.ExtraBold,
                    color = rankColor,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "${user.unitName} • ${user.badge}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = IndiaLocaleUtil.formatInrShort(user.achievedAmount), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = SuccessGreen)
                Text(text = "${user.points} XP (${user.conversionRate}%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
