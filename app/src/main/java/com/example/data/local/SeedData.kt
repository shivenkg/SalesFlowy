package com.example.data.local

import com.example.data.model.*

object SeedData {

    val users = listOf(
        UserEntity(
            id = "EMP-ZL-01",
            name = "Rajesh Khanna",
            email = "rajesh.khanna@salesorbit.corp",
            phone = "9876543210",
            role = PersonaType.ZONAL_LEAD.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 2500000.0,
            achievedMonthly = 2150000.0,
            points = 8450,
            customPermissions = "VIEW_ACTIVITIES,APPROVE_BEATS,APPROVE_MERCHANTS,VIEW_HIERARCHY,MANAGE_ADMIN,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-RH-02",
            name = "Priya Sharma",
            email = "priya.sharma@salesorbit.corp",
            phone = "9876543211",
            role = PersonaType.REGIONAL_HEAD.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 1200000.0,
            achievedMonthly = 1040000.0,
            points = 6200,
            customPermissions = "VIEW_ACTIVITIES,APPROVE_BEATS,APPROVE_MERCHANTS,VIEW_HIERARCHY,MANAGE_ADMIN,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-AM-03",
            name = "Vikram Malhotra",
            email = "vikram.m@salesorbit.corp",
            phone = "9876543212",
            role = PersonaType.HUB_AREA_MANAGER.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 550000.0,
            achievedMonthly = 480000.0,
            points = 4900,
            customPermissions = "VIEW_ACTIVITIES,APPROVE_BEATS,APPROVE_MERCHANTS,VIEW_HIERARCHY,ADD_LEADS,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-EX-04",
            name = "Amit Verma",
            email = "amit.verma@salesorbit.corp",
            phone = "9876543213",
            role = PersonaType.SPOKE_FRONTLINER.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 180000.0,
            achievedMonthly = 152000.0,
            points = 3250,
            customPermissions = "VIEW_ACTIVITIES,EXECUTE_BEAT,BOOK_ORDERS,SUBMIT_BEAT,ADD_LEADS,ONBOARD_MERCHANTS,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-EX-05",
            name = "Neha Gupta",
            email = "neha.gupta@salesorbit.corp",
            phone = "9876543214",
            role = PersonaType.SPOKE_FRONTLINER.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 160000.0,
            achievedMonthly = 148000.0,
            points = 2950,
            customPermissions = "VIEW_ACTIVITIES,EXECUTE_BEAT,BOOK_ORDERS,SUBMIT_BEAT,ADD_LEADS,ONBOARD_MERCHANTS,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-EX-06",
            name = "Rajesh Rao",
            email = "rajesh.rao@salesorbit.corp",
            phone = "9876543215",
            role = PersonaType.SPOKE_FRONTLINER.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 190000.0,
            achievedMonthly = 135000.0,
            points = 2400,
            customPermissions = "VIEW_ACTIVITIES,EXECUTE_BEAT,BOOK_ORDERS,SUBMIT_BEAT,ADD_LEADS,ONBOARD_MERCHANTS,CLAIM_REWARDS"
        ),
        UserEntity(
            id = "EMP-ADM-00",
            name = "Neha Sen",
            email = "admin@salesorbit.corp",
            phone = "9876543200",
            role = PersonaType.ADMIN.name,
            zoneId = "ZONE-NORTH",
            regionId = "REGION-NCR",
            hubId = "HUB-DELHI-CTR",
            spokeId = "SPOKE-CP",
            targetMonthly = 0.0,
            achievedMonthly = 0.0,
            points = 1000,
            customPermissions = AppPermission.entries.joinToString(",") { it.code }
        )
    )

    val hierarchyUnits = listOf(
        HierarchyUnitEntity(
            id = "ZONE-NORTH",
            name = "North Zone Headquarters",
            type = HierarchyType.ZONE.name,
            parentId = null,
            leadPersonName = "Rajesh Khanna",
            activeFrontliners = 48,
            monthlyTarget = 2500000.0,
            achievedTarget = 2150000.0
        ),
        HierarchyUnitEntity(
            id = "REGION-NCR",
            name = "NCR Capital Region",
            type = HierarchyType.REGION.name,
            parentId = "ZONE-NORTH",
            leadPersonName = "Priya Sharma",
            activeFrontliners = 22,
            monthlyTarget = 1200000.0,
            achievedTarget = 1040000.0
        ),
        HierarchyUnitEntity(
            id = "HUB-DELHI-CTR",
            name = "Central Delhi Operations Hub",
            type = HierarchyType.HUB.name,
            parentId = "REGION-NCR",
            leadPersonName = "Vikram Malhotra",
            activeFrontliners = 8,
            monthlyTarget = 550000.0,
            achievedTarget = 480000.0
        ),
        HierarchyUnitEntity(
            id = "SPOKE-CP",
            name = "Connaught Place Commercial Spoke",
            type = HierarchyType.SPOKE.name,
            parentId = "HUB-DELHI-CTR",
            leadPersonName = "Amit Verma",
            activeFrontliners = 3,
            monthlyTarget = 180000.0,
            achievedTarget = 152000.0
        ),
        HierarchyUnitEntity(
            id = "SPOKE-NP",
            name = "Nehru Place Tech Spoke",
            type = HierarchyType.SPOKE.name,
            parentId = "HUB-DELHI-CTR",
            leadPersonName = "Suresh Patel",
            activeFrontliners = 4,
            monthlyTarget = 200000.0,
            achievedTarget = 175000.0
        )
    )

    val beatPlans = listOf(
        BeatPlanEntity(
            id = "BEAT-TODAY-01",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            hubId = "HUB-DELHI-CTR",
            title = "Connaught Core Commercial Beat",
            routeDate = "2026-10-05",
            status = BeatStatus.APPROVED.name,
            managerNotes = "Approved by Vikram. Focus on festive inventory bookings.",
            approvedBy = "Vikram Malhotra (Area Mgr)",
            totalStops = 4,
            completedStops = 1,
            targetCollection = 65000.0,
            achievedCollection = 24500.0
        ),
        BeatPlanEntity(
            id = "BEAT-PENDING-02",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            hubId = "HUB-DELHI-CTR",
            title = "Barakhamba Wholesaler Expansion Route",
            routeDate = "2026-10-06",
            status = BeatStatus.PENDING_APPROVAL.name,
            managerNotes = "Awaiting review for distributor coverage.",
            approvedBy = "",
            totalStops = 5,
            completedStops = 0,
            targetCollection = 90000.0,
            achievedCollection = 0.0
        ),
        BeatPlanEntity(
            id = "BEAT-PAST-03",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            hubId = "HUB-DELHI-CTR",
            title = "Janpath Retail High-Street Beat",
            routeDate = "2026-10-03",
            status = BeatStatus.COMPLETED.name,
            managerNotes = "Target exceeded by 15%",
            approvedBy = "Vikram Malhotra",
            totalStops = 4,
            completedStops = 4,
            targetCollection = 50000.0,
            achievedCollection = 57500.0
        )
    )

    val beatStops = listOf(
        BeatStopEntity(
            id = "STOP-01",
            beatId = "BEAT-TODAY-01",
            stopOrder = 1,
            customerName = "Metro Mart Superstore",
            customerCategory = "Supermarket",
            address = "Block C, Connaught Place, New Delhi",
            phone = "+91 98112 34567",
            latitude = 28.6315,
            longitude = 77.2167,
            status = VisitStatus.COMPLETED.name,
            plannedTime = "10:00 AM",
            checkInTime = "10:05 AM",
            checkOutTime = "10:45 AM",
            checkInLat = 28.6314,
            checkInLng = 77.2168,
            interactionSummary = "Met store owner Mr. Bansal. Reviewed FMCG restock requirements and promotional shelf placement.",
            customerQuery = "Requested revised dispatch timelines for bulk beverage packages.",
            customerFeedback = "Extremely happy with previous delivery speed.",
            sentiment = "Positive",
            orderValue = 24500.0
        ),
        BeatStopEntity(
            id = "STOP-02",
            beatId = "BEAT-TODAY-01",
            stopOrder = 2,
            customerName = "Rajdhani Mega Provisions",
            customerCategory = "Wholesaler",
            address = "Inner Circle, Near Gate 4, New Delhi",
            phone = "+91 98223 45678",
            latitude = 28.6289,
            longitude = 77.2234,
            status = VisitStatus.PENDING.name,
            plannedTime = "11:30 AM",
            checkInTime = null,
            checkOutTime = null,
            orderValue = 0.0
        ),
        BeatStopEntity(
            id = "STOP-03",
            beatId = "BEAT-TODAY-01",
            stopOrder = 3,
            customerName = "Grand Luxe Gourmet Hub",
            customerCategory = "Enterprise Partner",
            address = "Janpath Lane, Opp. Royal Plaza, New Delhi",
            phone = "+91 98334 56789",
            latitude = 28.6250,
            longitude = 77.2185,
            status = VisitStatus.PENDING.name,
            plannedTime = "02:15 PM",
            checkInTime = null,
            checkOutTime = null,
            orderValue = 0.0
        ),
        BeatStopEntity(
            id = "STOP-04",
            beatId = "BEAT-TODAY-01",
            stopOrder = 4,
            customerName = "QuickBite Express Corner",
            customerCategory = "Convenience Chain",
            address = "K.G. Marg Crossroad, New Delhi",
            phone = "+91 98445 67890",
            latitude = 28.6210,
            longitude = 77.2240,
            status = VisitStatus.PENDING.name,
            plannedTime = "04:30 PM",
            checkInTime = null,
            checkOutTime = null,
            orderValue = 0.0
        )
    )

    val leads = listOf(
        LeadEntity(
            id = "LEAD-01",
            customerName = "Apex Global Hypermarkets",
            contactPerson = "Karan Singhania (VP Procurement)",
            phone = "+91 99111 22233",
            email = "karan@apexhyper.com",
            stage = LeadStage.NEGOTIATION.name,
            dealValue = 75000.0,
            priority = LeadPriority.CRITICAL.name,
            conversionDeadline = "Today, 5:00 PM",
            deadlineHoursRemaining = 2,
            assignedSpoke = "Connaught Spoke",
            assignedExecutiveName = "Amit Verma",
            notes = "Contract terms finalized at 8% volume discount. Final approval needed before evening budget cutoff.",
            isAlertActive = true
        ),
        LeadEntity(
            id = "LEAD-02",
            customerName = "Sunbeam Retail Outlets (12 Stores)",
            contactPerson = "Sunil Kapoor",
            phone = "+91 99222 33344",
            email = "procure@sunbeam.in",
            stage = LeadStage.PROPOSAL.name,
            dealValue = 120000.0,
            priority = LeadPriority.HIGH.name,
            conversionDeadline = "Tomorrow, 12:00 PM",
            deadlineHoursRemaining = 18,
            assignedSpoke = "Connaught Spoke",
            assignedExecutiveName = "Amit Verma",
            notes = "Submitted commercial quotation. Competitor offering credit terms; we are pitching faster SLA.",
            isAlertActive = true
        ),
        LeadEntity(
            id = "LEAD-03",
            customerName = "Vanguard FMCG Distribution",
            contactPerson = "Mohit Chawla",
            phone = "+91 99333 44455",
            email = "mohit@vanguardfmcg.org",
            stage = LeadStage.QUALIFIED.name,
            dealValue = 45000.0,
            priority = LeadPriority.HIGH.name,
            conversionDeadline = "Oct 7, 6:00 PM",
            deadlineHoursRemaining = 48,
            assignedSpoke = "Connaught Spoke",
            assignedExecutiveName = "Amit Verma",
            notes = "Audited warehouse facility. Ready for product pilot catalog presentation.",
            isAlertActive = false
        ),
        LeadEntity(
            id = "LEAD-04",
            customerName = "Heritage Organic Superstores",
            contactPerson = "Dr. Anita Roy",
            phone = "+91 99444 55566",
            email = "anita@heritageorganic.in",
            stage = LeadStage.WON.name,
            dealValue = 95000.0,
            priority = LeadPriority.MEDIUM.name,
            conversionDeadline = "Closed Won",
            deadlineHoursRemaining = 0,
            assignedSpoke = "Connaught Spoke",
            assignedExecutiveName = "Amit Verma",
            notes = "Annual purchase agreement signed. Initial shipment dispatched.",
            isAlertActive = false
        ),
        LeadEntity(
            id = "LEAD-05",
            customerName = "CityMart Express",
            contactPerson = "Devendra Joshi",
            phone = "+91 99555 66677",
            email = "dev@citymart.com",
            stage = LeadStage.CONTACTED.name,
            dealValue = 28000.0,
            priority = LeadPriority.MEDIUM.name,
            conversionDeadline = "Oct 9, 3:00 PM",
            deadlineHoursRemaining = 96,
            assignedSpoke = "Nehru Place Spoke",
            assignedExecutiveName = "Suresh Patel",
            notes = "Introductory pitch deck shared.",
            isAlertActive = false
        )
    )

    val prospectCustomers = listOf(
        ProspectCustomerEntity(
            id = "PROSPECT-01",
            businessName = "Evergreen Mega Mart",
            contactPerson = "Ramesh Gupta",
            phone = "+91 98989 12345",
            email = "ramesh@evergreenmart.in",
            category = "Supermarket Chain",
            address = "Plot 42, Connaught Outer Circle, New Delhi",
            latitude = 28.6340,
            longitude = 77.2201,
            gstNumber = "07AAAAA9876B1Z2",
            tradeLicenseNo = "DEL-TL-2026-9041",
            hasShopPhoto = true,
            hasKycDoc = true,
            status = "SUBMITTED_FOR_APPROVAL",
            submittedBy = "EMP-EX-04",
            submittedByName = "Amit Verma",
            managerNotes = "",
            submittedDate = "Today, 09:15 AM"
        ),
        ProspectCustomerEntity(
            id = "PROSPECT-02",
            businessName = "Zenith Wholesale Traders",
            contactPerson = "Harpreet Singh",
            phone = "+91 98787 54321",
            email = "harpreet@zenithwholesalers.com",
            category = "Wholesaler",
            address = "Gali 3, Sadar Bazar Depot, Central Delhi",
            latitude = 28.6562,
            longitude = 77.2144,
            gstNumber = "07BBBBB4567C1Z3",
            tradeLicenseNo = "DEL-TL-2026-3829",
            hasShopPhoto = true,
            hasKycDoc = true,
            status = "APPROVED",
            submittedBy = "EMP-EX-04",
            submittedByName = "Amit Verma",
            managerNotes = "Verified GST and premises. Approved for onboarding.",
            submittedDate = "Yesterday"
        )
    )

    val alertNotifications = listOf(
        AlertNotificationEntity(
            id = "ALERT-01",
            title = "High-Priority Lead Deadline (2 Hours)",
            message = "Apex Global Hypermarkets negotiation window closes at 5:00 PM today! Finalize discount terms immediately.",
            type = "DEADLINE_ALERT",
            priority = "CRITICAL",
            timestamp = "10 mins ago",
            isRead = false,
            relatedId = "LEAD-01"
        ),
        AlertNotificationEntity(
            id = "ALERT-02",
            title = "Beat Plan Approved",
            message = "Area Manager Vikram Malhotra approved your Connaught Core Commercial Beat for today.",
            type = "BEAT_APPROVAL",
            priority = "HIGH",
            timestamp = "1 hour ago",
            isRead = false,
            relatedId = "BEAT-TODAY-01"
        ),
        AlertNotificationEntity(
            id = "ALERT-03",
            title = "New Prospect Verification Pending",
            message = "Evergreen Mega Mart documents submitted by Amit Verma awaiting Hub Manager approval.",
            type = "PROSPECT_APPROVAL",
            priority = "NORMAL",
            timestamp = "2 hours ago",
            isRead = true,
            relatedId = "PROSPECT-01"
        ),
        AlertNotificationEntity(
            id = "ALERT-04",
            title = "Monthly Target Milestone (84%)",
            message = "Congratulations! You have reached 84% of your monthly sales revenue quota. Only $28k remaining.",
            type = "TARGET_ACHIEVED",
            priority = "HIGH",
            timestamp = "Yesterday",
            isRead = true
        )
    )

    val leaderboards = listOf(
        // Frontliners
        LeaderboardUser(
            rank = 1,
            name = "Amit Verma",
            role = "Sales Executive",
            unitName = "Connaught Spoke",
            achievedAmount = 152000.0,
            targetAmount = 180000.0,
            conversionRate = 84,
            points = 3250,
            badge = "Pipeline Ace",
            tier = "Frontliners"
        ),
        LeaderboardUser(
            rank = 2,
            name = "Suresh Patel",
            role = "Sales Executive",
            unitName = "Nehru Place Spoke",
            achievedAmount = 175000.0,
            targetAmount = 200000.0,
            conversionRate = 82,
            points = 3120,
            badge = "Target Crusher",
            tier = "Frontliners"
        ),
        LeaderboardUser(
            rank = 3,
            name = "Deepak Joshi",
            role = "Sales Executive",
            unitName = "Noida Hub Spoke",
            achievedAmount = 140000.0,
            targetAmount = 170000.0,
            conversionRate = 79,
            points = 2890,
            badge = "Beat Master",
            tier = "Frontliners"
        ),
        LeaderboardUser(
            rank = 4,
            name = "Kavita Rao",
            role = "Sales Executive",
            unitName = "Gurugram Spoke",
            achievedAmount = 135000.0,
            targetAmount = 175000.0,
            conversionRate = 77,
            points = 2740,
            badge = "Pioneer",
            tier = "Frontliners"
        ),
        // Hubs
        LeaderboardUser(
            rank = 1,
            name = "Central Delhi Hub",
            role = "Area Unit",
            unitName = "Lead: Vikram Malhotra",
            achievedAmount = 480000.0,
            targetAmount = 550000.0,
            conversionRate = 87,
            points = 8900,
            badge = "Hub Champion",
            tier = "Hub Units"
        ),
        LeaderboardUser(
            rank = 2,
            name = "South Delhi Hub",
            role = "Area Unit",
            unitName = "Lead: Ananya Iyer",
            achievedAmount = 430000.0,
            targetAmount = 520000.0,
            conversionRate = 82,
            points = 8200,
            badge = "Top Performer",
            tier = "Hub Units"
        ),
        // Regions
        LeaderboardUser(
            rank = 1,
            name = "NCR Capital Region",
            role = "Regional Unit",
            unitName = "Lead: Priya Sharma",
            achievedAmount = 1040000.0,
            targetAmount = 1200000.0,
            conversionRate = 86,
            points = 18500,
            badge = "Region MVP",
            tier = "Regions"
        ),
        LeaderboardUser(
            rank = 2,
            name = "Punjab & Haryana Region",
            role = "Regional Unit",
            unitName = "Lead: Gurpreet Singh",
            achievedAmount = 890000.0,
            targetAmount = 1100000.0,
            conversionRate = 80,
            points = 16200,
            badge = "Rising Region",
            tier = "Regions"
        )
    )

    val badges = listOf(
        BadgeItem(
            id = "BADGE-1",
            title = "Beat Master",
            description = "Completed 100% scheduled beat stops on time with GPS check-ins",
            level = "Gold",
            unlocked = true,
            iconName = "CheckCircle",
            currentProgress = 4,
            targetProgress = 4,
            xpBonus = 500
        ),
        BadgeItem(
            id = "BADGE-2",
            title = "Deadline Defender",
            description = "Converted 5 high-priority leads before deadline expiry",
            level = "Platinum",
            unlocked = true,
            iconName = "Alarm",
            currentProgress = 5,
            targetProgress = 5,
            xpBonus = 1000
        ),
        BadgeItem(
            id = "BADGE-3",
            title = "Century Closer",
            description = "Book single ground orders exceeding ₹50,000",
            level = "Gold",
            unlocked = true,
            iconName = "MonetizationOn",
            currentProgress = 3,
            targetProgress = 3,
            xpBonus = 750
        ),
        BadgeItem(
            id = "BADGE-4",
            title = "Pioneer Prospector",
            description = "Successfully onboard 10 verified prospective merchants",
            level = "Silver",
            unlocked = false,
            iconName = "PersonAdd",
            currentProgress = 6,
            targetProgress = 10,
            xpBonus = 600
        ),
        BadgeItem(
            id = "BADGE-5",
            title = "Zone MVP",
            description = "Achieve rank #1 across North Zone monthly pipeline",
            level = "Diamond",
            unlocked = false,
            iconName = "EmojiEvents",
            currentProgress = 1,
            targetProgress = 3,
            xpBonus = 2000
        )
    )

    val rewardCatalog = listOf(
        RewardItem(
            id = "REW-01",
            title = "Tech & Mobile Productivity Allowance",
            category = "Tech Perks",
            pointsCost = 1500,
            description = "₹8,000 corporate voucher for smartphones, accessories, and mobile devices reimbursement."
        ),
        RewardItem(
            id = "REW-02",
            title = "Quarterly Fuel & Travel Fleet Card",
            category = "Travel & Fuel",
            pointsCost = 2200,
            description = "₹12,000 HPCL/IOCL prepaid fuel card for seamless beat visits and field transport expenses."
        ),
        RewardItem(
            id = "REW-03",
            title = "Amazon India Enterprise Pass",
            category = "Shopping",
            pointsCost = 3000,
            description = "₹15,000 instant digital voucher applicable on Amazon India electronics and official merchandise."
        ),
        RewardItem(
            id = "REW-04",
            title = "President's Elite Club Annual Gala Ticket",
            category = "VIP Experience",
            pointsCost = 4500,
            description = "Exclusive all-expenses-paid VIP invitation to the national corporate leadership awards in Mumbai / Goa."
        ),
        RewardItem(
            id = "REW-05",
            title = "Taj / Oberoi 5-Star Weekend Retreat",
            category = "Wellness",
            pointsCost = 6000,
            description = "2-night luxury heritage stay for two with spa & fine dining dining package."
        )
    )

    val milestones = listOf(
        MilestoneProgress(
            id = "MS-01",
            title = "Monthly Target Quota",
            currentCount = 84,
            targetCount = 100,
            unit = "%",
            rewardPoints = 800,
            badgeUnlocked = "Target Crusher"
        ),
        MilestoneProgress(
            id = "MS-02",
            title = "Field Beat GPS Check-In",
            currentCount = 18,
            targetCount = 20,
            unit = "visits",
            rewardPoints = 500,
            badgeUnlocked = "Precision Master"
        ),
        MilestoneProgress(
            id = "MS-03",
            title = "Verified Merchant Onboardings",
            currentCount = 6,
            targetCount = 10,
            unit = "merchants",
            rewardPoints = 600,
            badgeUnlocked = "Pioneer Prospector"
        )
    )

    val auditLogs = listOf(
        AuditTrailEntity(
            id = "AUDIT-001",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            actionType = "GPS_CHECK_IN",
            entityTitle = "Stop 1: Metro Mart Connaught Place",
            description = "Field GPS verified check-in recorded within 12 meters radius of geofence perimeter.",
            latitude = 28.6328,
            longitude = 77.2197,
            ipAddress = "172.56.21.9",
            timestamp = "Today, 09:32 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-002",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            actionType = "ORDER_BOOKED",
            entityTitle = "Sales Order ORD-4921 (₹8,500)",
            description = "Captured order with SKU line items (3x Premium Espresso Roast, 2x Masala Chai Concentrate).",
            latitude = 28.6328,
            longitude = 77.2197,
            ipAddress = "103.21.58.12",
            timestamp = "Today, 10:05 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-003",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            actionType = "VISIT_COMPLETED",
            entityTitle = "Check-Out: Metro Mart",
            description = "Visit concluded. Feedback: Positive product sentiment, request for quarterly payment terms.",
            latitude = 28.6328,
            longitude = 77.2197,
            ipAddress = "172.56.21.9",
            timestamp = "Today, 10:14 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-004",
            executiveId = "EMP-EX-05",
            executiveName = "Neha Gupta",
            actionType = "GPS_CHECK_IN",
            entityTitle = "Stop 2: Grand Provisions Wholesale",
            description = "Field GPS check-in at Barakhamba Road.",
            latitude = 28.6304,
            longitude = 77.2241,
            ipAddress = "172.56.22.4",
            timestamp = "Today, 10:30 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-005",
            executiveId = "EMP-EX-05",
            executiveName = "Neha Gupta",
            actionType = "LEAD_STAGE_UPDATED",
            entityTitle = "Apex Global Retail (₹1,20,000)",
            description = "Advanced pipeline stage from 'Proposal Sent' to 'Negotiation' with commercial terms.",
            latitude = 28.6304,
            longitude = 77.2241,
            ipAddress = "103.21.58.19",
            timestamp = "Today, 11:15 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-006",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            actionType = "MERCHANT_ONBOARDED",
            entityTitle = "KYC Upload: Sunrise Superstores",
            description = "Submitted merchant onboarding with GSTIN 07AAAAA0000A1Z5 & trade license document.",
            latitude = 28.6341,
            longitude = 77.2155,
            ipAddress = "172.56.21.9",
            timestamp = "Today, 11:45 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-007",
            executiveId = "EMP-EX-06",
            executiveName = "Rajesh Rao",
            actionType = "OFFLINE_SYNC",
            entityTitle = "Batch Offline Queue Upload (4 items)",
            description = "Synchronized 2 GPS check-ins, 1 lead creation, and 1 order recorded in low-connectivity zone.",
            latitude = 28.6291,
            longitude = 77.2180,
            ipAddress = "172.56.24.11",
            timestamp = "Today, 12:20 PM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-008",
            executiveId = "EMP-AM-03",
            executiveName = "Vikram Malhotra",
            actionType = "BEAT_APPROVED",
            entityTitle = "Beat Approval: Connaught Central Route",
            description = "Area Manager approved today's field beat plan with 5 stops and ₹50,000 collection target.",
            latitude = 28.6315,
            longitude = 77.2167,
            ipAddress = "103.21.58.1",
            timestamp = "Today, 08:45 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        ),
        AuditTrailEntity(
            id = "AUDIT-009",
            executiveId = "EMP-EX-04",
            executiveName = "Amit Verma",
            actionType = "LOGIN",
            entityTitle = "Corporate Executive Session Authenticated",
            description = "Mobile client authenticated via biometric/OTP token. Hardware device: Samsung S24 Ultra.",
            latitude = 28.6315,
            longitude = 77.2167,
            ipAddress = "172.56.21.9",
            timestamp = "Today, 08:30 AM",
            riskLevel = "NORMAL",
            spokeId = "SPOKE-CP"
        )
    )
}
