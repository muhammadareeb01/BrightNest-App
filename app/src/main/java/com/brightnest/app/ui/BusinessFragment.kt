package com.brightnest.app.ui

import com.brightnest.app.R

class BusinessFragment : TopicGuideFragment() {
    override val screenTitle = "Business Guide"
    override val accentColorRes = R.color.sage
    override val heroIcon = "📈"
    override val heroTitle = "Startup 101"
    override val heroSubtitle = "Core principles for founders"
    override val cardIcon = "📚"
    override val modalIcon = "⚡"

    override val topics = listOf(
        Topic("Validating an idea", "Don't build before you ask", "The biggest mistake entrepreneurs make is building a product in secret and hoping people buy it. Instead, start by validating the problem. Create a simple landing page describing your solution and see if people will give you their email or pre-order. Talk to 10 potential customers and ask them how they currently solve the problem. If they aren't actively trying to solve it, it's not a big enough pain point to build a business around."),
        Topic("Lean MVP launch", "Ship embarrassing first versions", "A Minimum Viable Product (MVP) should be the absolute smallest thing you can build to deliver value. If your app connects dog walkers with owners, your MVP could just be a WhatsApp group or an Airtable database. Don't spend months coding features people might not use. Ship fast, get real users, listen to their feedback, and iterate. If you are not slightly embarrassed by your first release, you launched too late."),
        Topic("Pricing strategies 101", "Charge more than you think", "Pricing dictates your business model. If you charge \$5/mo, you need thousands of customers to survive (which requires massive marketing). If you charge \$500/mo, you only need a few (which requires direct sales). Always try to price based on the value you provide, not your costs. If your software saves a company \$10,000 a month, charging \$1,000 is a bargain. Offer 3 tiers: a basic, a standard, and an anchor (very high price) to make the standard look like the best deal."),
        Topic("Customer discovery", "How to interview users", "When interviewing potential customers, never ask 'Would you buy this?' People want to be nice and will lie to you. Instead, ask about their past behavior. 'Tell me about the last time you tried to solve [problem].' 'How much did you spend?' 'What frustrated you about that process?' Past behavior is the only reliable predictor of future behavior. Listen more than you talk, and look for emotion—if they aren't visibly frustrated by the problem, move on."),
        Topic("Marketing on zero budget", "Hustle beats capital early on", "You don't need Facebook ads to get your first 100 customers. Go where your users already hang out. If they are developers, go to GitHub, Reddit, or Hacker News. If they are local businesses, walk in and shake hands. Build in public on Twitter or LinkedIn to attract early adopters. Content marketing and SEO take time but compound over years. The unscalable things you do early on (like personally emailing 500 prospects) are what build the foundation."),
        Topic("Building a brand voice", "Stand for something specific", "Your brand is not your logo; it's how people feel when they interact with your company. Determine your core archetype. Are you the rebellious challenger? The wise sage? The helpful neighbor? Keep this consistent across your website, emails, and support channels. Use plain language. Eradicate corporate jargon like 'synergy' and 'next-gen solutions'. Talk to your customers like they are smart friends."),
        Topic("Cash flow basics", "Revenue is vanity, cash is reality", "Many profitable businesses go bankrupt simply because they run out of cash before the invoices get paid. Track your 'runway'—how many months you can survive if revenue goes to zero. Always separate personal and business finances. Set aside 25-30% of every dollar for taxes immediately. Negotiate longer payment terms with your suppliers and shorter payment terms with your customers. Keep your fixed costs as low as possible for as long as possible."),
        Topic("Your first employee", "When and who to hire", "Hire when the pain of not hiring is greater than the cost and effort of training someone. Your first hire should ideally be someone who takes over the repetitive tasks you already know how to do, freeing you up to focus on growth. Alternatively, hire someone who possesses a critical skill you lack completely (like a technical co-founder if you're sales-focused). Always hire for adaptability and culture fit early on; specialists come later.")
    )
}
