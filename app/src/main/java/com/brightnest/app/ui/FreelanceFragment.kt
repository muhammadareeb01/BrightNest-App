package com.brightnest.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brightnest.app.R

class FreelanceFragment : TopicGuideFragment() {
    override val screenTitle = "Freelance Guide"
    override val accentColorRes = R.color.accent
    override val heroIcon = "💼"
    override val heroTitle = "Build Your Career"
    override val heroSubtitle = "Essential tips for freelance success"
    override val cardIcon = "📖"
    override val modalIcon = "⭐"

    override val topics = listOf(
        Topic("Building your first portfolio", "Showcase your best work", "Your portfolio is your storefront. You don't need dozens of projects—just 3 to 5 high-quality case studies that solve real problems. Outline the challenge, your role, the solution you provided, and the measurable results. Keep it visual and easy to scan. Use platforms like Behance, Dribbble, or a simple Notion page if you don't have a personal website yet. Focus on quality over quantity, and always tailor your portfolio to the type of clients you want to attract."),
        Topic("Pricing your services", "How much should you charge?", "Pricing is often the hardest part of freelancing. Start by calculating your minimum acceptable hourly rate based on your living expenses, taxes, and software costs. Then, consider transitioning to value-based or project-based pricing. Instead of charging for time, charge for the value your work brings to the client's business. Always ask for the client's budget upfront, and never be afraid to quote higher than you feel comfortable—it leaves room for negotiation and filters out bad clients."),
        Topic("Finding clients on Upwork/Fiverr", "Stand out in crowded marketplaces", "To succeed on freelance marketplaces, your profile must be hyper-specific. Don't be a 'General Developer'—be a 'Shopify E-commerce Expert'. Use a professional photo and write a compelling bio that focuses on client outcomes, not just your skills. When applying for jobs, avoid generic copy-pasted proposals. Address the client's specific problem mentioned in the job description, offer a quick insight or solution right away, and include relevant portfolio links."),
        Topic("Writing winning proposals", "Craft pitches that get responses", "A winning proposal is about the client, not you. Start with a strong hook that acknowledges their specific need. Follow with a brief outline of how you will solve their problem. Provide social proof by linking 1-2 highly relevant past projects. End with a clear call-to-action (e.g., 'Are you available for a 10-minute chat tomorrow to discuss the timeline?'). Keep it concise—clients are busy and will skip long, rambling messages."),
        Topic("Managing client expectations", "Under-promise and over-deliver", "Clear communication prevents 90% of freelance problems. Set boundaries early: define your working hours, response times, and the exact scope of work in a written contract. Be upfront about what is NOT included in the project. If a client asks for extra work, politely inform them that it falls outside the original scope and provide a quote for the additional tasks. Always provide regular status updates before they have to ask for them."),
        Topic("Time tracking & productivity", "Stay focused and bill accurately", "Treat your freelance work like a real business. Use tools like Toggl, Clockify, or simple timers to track exactly where your hours go—even for fixed-price projects. This helps you understand your true hourly rate and estimate future projects better. Use the Pomodoro technique (25 mins work, 5 mins rest) to maintain focus. Set specific 'deep work' hours where notifications are turned off to handle complex tasks without interruption."),
        Topic("Getting paid safely", "Protect yourself from non-payment", "Never start work without a deposit. A standard practice is 50% upfront, 50% upon completion. For larger projects, use milestone payments (e.g., 30% upfront, 30% midway, 40% at launch). Always use a contract—even a simple one—that outlines deliverables and payment terms. Use professional invoicing software (Stripe, PayPal, Wave) that allows clients to pay via credit card. If a client hesitates to pay a deposit, consider it a major red flag."),
        Topic("Scaling beyond one client", "Building a sustainable business", "Once you have steady work, it's time to scale. Start by raising your rates for new clients by 15-20%. Build recurring revenue by offering maintenance retainers or monthly consulting packages. Systematize your processes: create templates for proposals, invoices, and onboarding emails. Ask happy clients for referrals and testimonials. Eventually, you can consider subcontracting parts of the work to other freelancers to take on larger projects.")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}
