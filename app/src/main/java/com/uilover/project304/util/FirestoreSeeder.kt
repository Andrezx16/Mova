package com.uilover.project304.util

import com.google.firebase.firestore.FirebaseFirestore
import com.uilover.project304.data.model.Property
import com.uilover.project304.data.model.PropertyCategory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val propertiesCollection = firestore.collection("properties")

    suspend fun seedDatabase() {
        val properties = getSampleProperties()

        for (property in properties) {
            try {
                propertiesCollection.document(property.id).set(property.toMap()).await()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getSampleProperties(): List<Property> {
        return listOf(
            Property(
                id = "feat-1",
                title = "Oceanfront Villa",
                price = 4250000.0,
                formattedPrice = "$4,250,000",
                address = "1234 Ocean Drive, Malibu, CA 90265",
                beds = 5,
                baths = 4.5,
                sqft = 6200,
                rating = 4.9,
                imageUrl = "https://images.unsplash.com/photo-1613490493576-7fde63acd811?w=800",
                category = PropertyCategory.VILLA,
                isFeatured = true,
                badge = "Featured",
                description = "Luxurious oceanfront villa with breathtaking views. This stunning property features floor-to-ceiling windows, a private infinity pool, and direct beach access. The gourmet kitchen includes top-of-the-line appliances and a wine cellar.",
                amenities = listOf("Pool", "Beach Access", "Wine Cellar", "Home Theater", "Smart Home")
            ),
            Property(
                id = "feat-2",
                title = "Modern Penthouse",
                price = 3500000.0,
                formattedPrice = "$3,500,000",
                address = "567 Park Avenue, New York, NY 10022",
                beds = 3,
                baths = 3.0,
                sqft = 4500,
                rating = 4.8,
                imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?w=800",
                category = PropertyCategory.PENTHOUSE,
                isFeatured = true,
                badge = "New Construction",
                description = "Stunning penthouse with panoramic city views. Features include a private terrace, chef's kitchen, and luxury finishes throughout. Building amenities include doorman, gym, and rooftop access.",
                amenities = listOf("Terrace", "Concierge", "Gym", "Rooftop Access", "Doorman")
            ),
            Property(
                id = "feat-3",
                title = "Luxury Townhouse",
                price = 2800000.0,
                formattedPrice = "$2,800,000",
                address = "890 Beacon Street, Boston, MA 02115",
                beds = 4,
                baths = 3.5,
                sqft = 3800,
                rating = 4.7,
                imageUrl = "https://images.unsplash.com/photo-1600596542815-ffad4c1539a9?w=800",
                category = PropertyCategory.TOWNHOUSE,
                isFeatured = true,
                badge = "Price Reduced",
                description = "Elegant brownstone townhouse in historic neighborhood. Recently renovated with modern amenities while preserving original character. Features include a private garden and rooftop deck.",
                amenities = listOf("Garden", "Rooftop Deck", "Parking", "Fireplace", "Wine Room")
            ),
            Property(
                id = "feat-4",
                title = "Contemporary House",
                price = 1950000.0,
                formattedPrice = "$1,950,000",
                address = "2345 Sunset Boulevard, Los Angeles, CA 90026",
                beds = 4,
                baths = 3.0,
                sqft = 3200,
                rating = 4.6,
                imageUrl = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800",
                category = PropertyCategory.HOUSE,
                isFeatured = true,
                badge = "Open House",
                description = "Sleek contemporary home in prime location. Open floor plan with high ceilings, gourmet kitchen, and seamless indoor-outdoor living. Private backyard with pool and entertainment area.",
                amenities = listOf("Pool", "Smart Home", "Solar Panels", "EV Charging", "Home Office")
            ),
            Property(
                id = "rec-1",
                title = "Beachfront Apartment",
                price = 850000.0,
                formattedPrice = "$850,000",
                address = "789 Pacific Coast Highway, Santa Monica, CA 90405",
                beds = 2,
                baths = 2.0,
                sqft = 1800,
                rating = 4.5,
                imageUrl = "https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?w=800",
                category = PropertyCategory.APARTMENT,
                isFeatured = false,
                description = "Beautiful beachfront apartment with ocean views. Modern finishes, spacious balcony, and resort-style amenities. Walking distance to beach and shops.",
                amenities = listOf("Beach Access", "Pool", "Gym", "Parking", "Concierge")
            ),
            Property(
                id = "rec-2",
                title = "Mountain Villa",
                price = 2200000.0,
                formattedPrice = "$2,200,000",
                address = "456 Alpine Road, Aspen, CO 81611",
                beds = 5,
                baths = 4.0,
                sqft = 5500,
                rating = 4.8,
                imageUrl = "https://images.unsplash.com/photo-1518780664697-55e3ad937233?w=800",
                category = PropertyCategory.VILLA,
                isFeatured = false,
                description = "Stunning mountain retreat with ski-in/ski-out access. Features include a hot tub, home theater, and wine cellar. Perfect for year-round luxury living.",
                amenities = listOf("Hot Tub", "Ski Access", "Home Theater", "Wine Cellar", "Fireplace")
            ),
            Property(
                id = "rec-3",
                title = "Urban Loft",
                price = 650000.0,
                formattedPrice = "$650,000",
                address = "321 Industrial Way, Brooklyn, NY 11222",
                beds = 1,
                baths = 1.0,
                sqft = 1200,
                rating = 4.4,
                imageUrl = "https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?w=800",
                category = PropertyCategory.APARTMENT,
                isFeatured = false,
                description = "Stylish industrial loft in vibrant neighborhood. Exposed brick, high ceilings, and large windows. Close to restaurants, galleries, and nightlife.",
                amenities = listOf("Rooftop Access", "Bike Storage", "Pet Friendly", "Gym", "Package Room")
            ),
            Property(
                id = "rec-4",
                title = "Garden Estate",
                price = 3800000.0,
                formattedPrice = "$3,800,000",
                address = "789 Garden Lane, Greenwich, CT 06830",
                beds = 6,
                baths = 5.5,
                sqft = 8000,
                rating = 4.9,
                imageUrl = "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800",
                category = PropertyCategory.HOUSE,
                isFeatured = false,
                description = "Magnificent estate on 5 acres of manicured grounds. Features include a guest house, pool house, and tennis court. Perfect for entertaining.",
                amenities = listOf("Pool", "Tennis Court", "Guest House", "Wine Cellar", "Helipad")
            ),
            Property(
                id = "rec-5",
                title = "Skyline Penthouse",
                price = 5200000.0,
                formattedPrice = "$5,200,000",
                address = "100 Skyline Drive, Chicago, IL 60601",
                beds = 4,
                baths = 4.0,
                sqft = 4800,
                rating = 4.7,
                imageUrl = "https://images.unsplash.com/photo-1600047509807-ba8f99d2cdde?w=800",
                category = PropertyCategory.PENTHOUSE,
                isFeatured = false,
                description = "Breathtaking penthouse with 360-degree city views. Private elevator, chef's kitchen, and multiple terraces. Building includes spa and private dining.",
                amenities = listOf("Private Elevator", "Spa", "Terrace", "Chef's Kitchen", "Wine Room")
            ),
            Property(
                id = "rec-6",
                title = "Coastal Cottage",
                price = 750000.0,
                formattedPrice = "$750,000",
                address = "456 Seaside Avenue, Cape Cod, MA 02657",
                beds = 3,
                baths = 2.0,
                sqft = 2000,
                rating = 4.6,
                imageUrl = "https://images.unsplash.com/photo-1600566753190-17f0baa2a6c3?w=800",
                category = PropertyCategory.HOUSE,
                isFeatured = false,
                description = "Charming coastal cottage with water views. Renovated with modern amenities while maintaining classic New England character. Private beach access.",
                amenities = listOf("Beach Access", "Deck", "Fireplace", "Garden", "Outdoor Shower")
            )
        )
    }
}
