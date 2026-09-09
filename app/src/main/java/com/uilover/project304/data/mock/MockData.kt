package com.uilover.project304.data.mock

import com.uilover.project304.R
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import com.uilover.project304.data.model.UserProfile

object MockData {
    val currentUser = UserProfile(
        name = "Sophia Vance",
        email = "sophia.vance@luxerealty.com",
        membershipStatus = "VIP Platinum",
        avatarRes = R.drawable.user_profile
    )

    val featuredProperties = listOf(
        Property(
            id = "feat-1",
            title = "The Beverly Modern",
            price = 4250000.0,
            formattedPrice = "$4,250,000",
            address = "Beverly Hills, CA",
            beds = 5,
            baths = 6.0,
            sqft = 5800,
            rating = 4.9,
            imageRes = R.drawable.the_beverly_modern,
            category = PropertyCategory.VILLA,
            isFeatured = true,
            badge = "Featured",
            description = "An architectural masterpiece offering sweeping canyon and city views, infinity pool, custom Italian millwork, and smart home automation.",
            amenities = listOf("Infinity Pool", "Wine Cellar", "Smart Home", "Spa", "6-Car Garage")
        ),
        Property(
            id = "feat-2",
            title = "Skyline Penthouse",
            price = 2100000.0,
            formattedPrice = "$2,100,000",
            address = "Downtown, NY",
            beds = 3,
            baths = 3.5,
            sqft = 2900,
            rating = 4.8,
            imageRes = R.drawable.skyline_penthouse,
            category = PropertyCategory.PENTHOUSE,
            isFeatured = true,
            badge = null,
            description = "Floor-to-ceiling glass wrapping around Manhattan skyline panoramas with private elevator entrance and 1,000 sqft wraparound terrace.",
            amenities = listOf("Private Elevator", "Terrace", "Concierge", "Fitness Center", "Valet")
        ),
        Property(
            id = "feat-3",
            title = "Pacific Coast Estate",
            price = 6500000.0,
            formattedPrice = "$6,500,000",
            address = "Malibu, CA",
            beds = 6,
            baths = 7.0,
            sqft = 7200,
            rating = 5.0,
            imageRes = R.drawable.pacific_coast_hwy_malibu,
            category = PropertyCategory.VILLA,
            isFeatured = true,
            badge = "Featured",
            description = "Direct beach access with panoramic ocean views, outdoor kitchen, temperature-controlled wine room, and home theater.",
            amenities = listOf("Beach Access", "Home Theater", "Outdoor Kitchen", "Pool", "Fire Pit")
        )
    )

    val recommendedProperties = listOf(
        Property(
            id = "rec-1",
            title = "Oakwood Retreat",
            price = 1450000.0,
            formattedPrice = "$1,450,000",
            address = "123 Maple St, Austin, TX",
            beds = 3,
            baths = 2.5,
            sqft = 2400,
            rating = 4.9,
            imageRes = R.drawable.oakwood_retreat,
            category = PropertyCategory.HOUSE,
            isFeatured = false,
            badge = "New Build",
            description = "Mid-century modern oasis blending warm wood finishes, expansive landscaped grounds, and open floor plan entertaining.",
            amenities = listOf("Landscaped Yard", "Chef's Kitchen", "Solar Panels", "2-Car Garage")
        ),
        Property(
            id = "rec-2",
            title = "Industrial Arts Loft",
            price = 895000.0,
            formattedPrice = "$895,000",
            address = "Arts District, LA",
            beds = 1,
            baths = 1.0,
            sqft = 1200,
            rating = 4.7,
            imageRes = R.drawable.industrial_arts_loft,
            category = PropertyCategory.APARTMENT,
            isFeatured = false,
            badge = null,
            description = "Authentic timber beams, exposed brickwork, 18-foot ceilings, and enormous factory windows flooding the space with natural light.",
            amenities = listOf("Exposed Brick", "High Ceilings", "Rooftop Pool", "Doorman")
        ),
        Property(
            id = "rec-3",
            title = "Historic Townhouse",
            price = 3150000.0,
            formattedPrice = "$3,150,000",
            address = "Back Bay, Boston",
            beds = 4,
            baths = 3.5,
            sqft = 3800,
            rating = 4.8,
            imageRes = R.drawable.historic_townhouse,
            category = PropertyCategory.TOWNHOUSE,
            isFeatured = false,
            badge = null,
            description = "Stately Victorian brownstone restored with timeless craftsmanship, modern luxury chef kitchen, private garden, and original fireplaces.",
            amenities = listOf("Private Garden", "Original Fireplace", "Wine Cellar", "Gourmet Kitchen")
        ),
        Property(
            id = "rec-4",
            title = "Azure Way Residence",
            price = 3850000.0,
            formattedPrice = "$3,850,000",
            address = "Azure Way, Beverly Hills",
            beds = 5,
            baths = 5.5,
            sqft = 5100,
            rating = 4.9,
            imageRes = R.drawable.azure_way_beverly_hills,
            category = PropertyCategory.HOUSE,
            isFeatured = false,
            badge = "New Build",
            description = "Crisp contemporary design featuring organic stone textures, floor-to-ceiling glass pocket doors, and seamless indoor-outdoor living.",
            amenities = listOf("Pool", "Pocket Doors", "Smart Home", "Guest House")
        ),
        Property(
            id = "rec-5",
            title = "Palm Drive Villa",
            price = 2750000.0,
            formattedPrice = "$2,750,000",
            address = "Palm Drive, Coral Gables",
            beds = 4,
            baths = 4.0,
            sqft = 4200,
            rating = 4.9,
            imageRes = R.drawable.palm_drive_coral_gables,
            category = PropertyCategory.VILLA,
            isFeatured = false,
            badge = "New Build",
            description = "Mediterranean revival estate surrounded by tropical palms, courtyards with fountains, and luxurious master suite loggias.",
            amenities = listOf("Courtyard Fountain", "Loggia", "Pool", "Summer Kitchen")
        ),
        Property(
            id = "rec-6",
            title = "Park Avenue Penthouse",
            price = 5400000.0,
            formattedPrice = "$5,400,000",
            address = "Park Avenue, New York",
            beds = 4,
            baths = 4.5,
            sqft = 4600,
            rating = 5.0,
            imageRes = R.drawable.park_avenue_penthouse,
            category = PropertyCategory.PENTHOUSE,
            isFeatured = false,
            badge = "Featured",
            description = "Pre-war grandeur meets ultra-modern luxury on prestigious Park Avenue with bespoke marble baths and dramatic views.",
            amenities = listOf("White Glove Service", "Private Elevator", "Library", "Fireplace")
        )
    )

    val savedProperties = listOf(
        Property(
            id = "saved-1",
            title = "Trousdale Estates Modern",
            price = 8450000.0,
            formattedPrice = "$8,450,000",
            address = "1234 Trousdale Estates, Beverly Hills, CA 90210",
            beds = 5,
            baths = 7.0,
            sqft = 8200,
            rating = 4.9,
            imageRes = R.drawable.trousdale_estates_beverly_hills,
            category = PropertyCategory.VILLA,
            isFeatured = true,
            badge = "New Construction",
            isFavorite = true,
            description = "Perched high in prestigious Trousdale Estates, this organic modern architectural marvel features sweeping city-to-ocean vistas, cantilevered infinity pool, and seamless indoor-outdoor luxury.",
            amenities = listOf("Infinity Pool", "City Views", "Home Theater", "Wine Cellar", "Smart Automation")
        ),
        Property(
            id = "saved-2",
            title = "Park Avenue Tower Penthouse",
            price = 12900000.0,
            formattedPrice = "$12,900,000",
            address = "432 Park Avenue, Penthouse 82, New York, NY",
            beds = 4,
            baths = 5.5,
            sqft = 4500,
            rating = 5.0,
            imageRes = R.drawable.park_avenue_penthouse,
            category = PropertyCategory.PENTHOUSE,
            isFeatured = true,
            badge = null,
            isFavorite = true,
            description = "Iconic soaring views of Central Park and the Manhattan skyline from 1,000 feet up. 12.5-foot ceilings, solid oak floors, and private elevator entry.",
            amenities = listOf("Central Park Views", "Private Elevator", "Concierge", "Spa", "Private Dining")
        ),
        Property(
            id = "saved-3",
            title = "Malibu Coastal Villa",
            price = 6200000.0,
            formattedPrice = "$6,200,000",
            address = "789 Pacific Coast Hwy, Malibu, CA 90265",
            beds = 3,
            baths = 4.0,
            sqft = 3800,
            rating = 4.8,
            imageRes = R.drawable.pacific_coast_hwy_malibu,
            category = PropertyCategory.VILLA,
            isFeatured = false,
            badge = "Price Reduced",
            isFavorite = true,
            description = "Clifftop architectural sanctuary above private cove with endless Pacific Ocean horizons, teak terraces, and direct private beach stairs.",
            amenities = listOf("Oceanfront", "Beach Access", "Teak Decks", "Wine Room", "Chef's Kitchen")
        ),
        Property(
            id = "saved-4",
            title = "The Beverly Modern",
            price = 4250000.0,
            formattedPrice = "$4,250,000",
            address = "Beverly Hills, CA",
            beds = 5,
            baths = 6.0,
            sqft = 5800,
            rating = 4.9,
            imageRes = R.drawable.the_beverly_modern,
            category = PropertyCategory.VILLA,
            isFeatured = true,
            badge = "Featured",
            isFavorite = true,
            description = "An architectural masterpiece offering sweeping canyon and city views, infinity pool, custom Italian millwork, and smart home automation.",
            amenities = listOf("Infinity Pool", "Wine Cellar", "Smart Home", "Spa", "6-Car Garage")
        )
    )

    val searchProperties = listOf(
        Property(
            id = "search-1",
            title = "Azure Way Villa",
            price = 2450000.0,
            formattedPrice = "$2,450,000",
            address = "123 Azure Way, Beverly Hills, CA",
            beds = 4,
            baths = 3.5,
            sqft = 3200,
            rating = 4.9,
            imageRes = R.drawable.azure_way_beverly_hills,
            category = PropertyCategory.HOUSE,
            isFeatured = false,
            badge = "New Construction",
            description = "Stunning contemporary estate in Beverly Hills featuring an open floor concept, expansive pool, chef kitchen, and private primary retreat.",
            amenities = listOf("Pool", "Chef Kitchen", "Smart Home", "2-Car Garage")
        ),
        Property(
            id = "search-2",
            title = "Skyline Blvd Residence",
            price = 1850000.0,
            formattedPrice = "$1,850,000",
            address = "88 Skyline Blvd, Apt 42B, Manhattan, NY",
            beds = 2,
            baths = 2.0,
            sqft = 1800,
            rating = 4.8,
            imageRes = R.drawable.skyline_blvd_apt_manhattan,
            category = PropertyCategory.APARTMENT,
            isFeatured = false,
            badge = null,
            description = "High-floor luxury corner residence featuring floor-to-ceiling glass, panoramic skyline views, custom built-ins, and 24/7 concierge.",
            amenities = listOf("Doorman", "Gym", "Concierge", "High Ceilings")
        ),
        Property(
            id = "search-3",
            title = "Palm Drive Courtyard Estate",
            price = 4100000.0,
            formattedPrice = "$4,100,000",
            address = "450 Palm Drive, Coral Gables, FL",
            beds = 5,
            baths = 4.5,
            sqft = 4500,
            rating = 4.9,
            imageRes = R.drawable.palm_drive_coral_gables,
            category = PropertyCategory.VILLA,
            isFeatured = false,
            badge = null,
            description = "Timeless Mediterranean luxury surrounded by majestic royal palms, fountain courtyard, heated pool, and expansive outdoor loggia.",
            amenities = listOf("Pool", "Courtyard", "Wine Cellar", "Summer Kitchen")
        ),
        Property(
            id = "search-4",
            title = "Lofts Avenue Studio",
            price = 950000.0,
            formattedPrice = "$950,000",
            address = "200 Lofts Ave, Unit 12, Seattle, WA",
            beds = 0, // Studio
            baths = 1.0,
            sqft = 850,
            rating = 4.7,
            imageRes = R.drawable.lofts_ave_unit_seattle,
            category = PropertyCategory.APARTMENT,
            isFeatured = false,
            badge = "Open House",
            description = "Chic minimalist open-concept studio loft with soaring ceilings, industrial factory windows, high-end European appliances, and polished concrete floors.",
            amenities = listOf("High Ceilings", "Rooftop Deck", "Parking", "WiFi")
        )
    )

    val allProperties: List<Property> = featuredProperties + recommendedProperties + savedProperties + searchProperties
}


